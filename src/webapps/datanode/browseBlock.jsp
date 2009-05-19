<%
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file 
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
%>
<%@ page
  contentType="text/html; charset=UTF-8"
  import="javax.servlet.*"
  import="javax.servlet.http.*"
  import="java.io.*"
  import="java.util.*"
  import="java.net.*"

  import="org.apache.hadoop.hdfs.*"
  import="org.apache.hadoop.hdfs.server.namenode.*"
  import="org.apache.hadoop.hdfs.protocol.*"
  import="org.apache.hadoop.security.AccessToken"
  import="org.apache.hadoop.security.AccessTokenHandler"
  import="org.apache.hadoop.util.*"
%>

<%!
  static final DataNode datanode = DataNode.getDataNode();

  public void generateFileDetails(JspWriter out, HttpServletRequest req) 
    throws IOException {

    long startOffset = 0;
    int datanodePort;

    final Long blockId = JspHelper.validateLong(req.getParameter("blockId"));
    if (blockId == null) {
      out.print("Invalid input (blockId absent)");
      return;
    }

    String datanodePortStr = req.getParameter("datanodePort");
    if (datanodePortStr == null) {
      out.print("Invalid input (datanodePort absent)");
      return;
    }
    datanodePort = Integer.parseInt(datanodePortStr);

    String namenodeInfoPortStr = req.getParameter("namenodeInfoPort");
    int namenodeInfoPort = -1;
    if (namenodeInfoPortStr != null)
      namenodeInfoPort = Integer.parseInt(namenodeInfoPortStr);

    final int chunkSizeToView = JspHelper.string2ChunkSizeToView(req.getParameter("chunkSizeToView"));

    String startOffsetStr = req.getParameter("startOffset");
    if (startOffsetStr == null || Long.parseLong(startOffsetStr) < 0)
      startOffset = 0;
    else startOffset = Long.parseLong(startOffsetStr);
    
    final String filename = JspHelper.validatePath(
        req.getParameter("filename"));
    if (filename == null) {
      out.print("Invalid input");
      return;
    }

    String blockSizeStr = req.getParameter("blockSize"); 
    long blockSize = 0;
    if (blockSizeStr == null || blockSizeStr.length() == 0) {
      out.print("Invalid input");
      return;
    } 
    blockSize = Long.parseLong(blockSizeStr);

    final DFSClient dfs = new DFSClient(datanode.getNameNodeAddr(), JspHelper.conf);
    List<LocatedBlock> blocks = 
      dfs.namenode.getBlockLocations(filename, 0, Long.MAX_VALUE).getLocatedBlocks();
    //Add the various links for looking at the file contents
    //URL for downloading the full file
    String downloadUrl = "http://" + req.getServerName() + ":" +
                         + req.getServerPort() + "/streamFile?" + "filename=" +
                         URLEncoder.encode(filename, "UTF-8");
    out.print("<a name=\"viewOptions\"></a>");
    out.print("<a href=\"" + downloadUrl + "\">Download this file</a><br>");
    
    DatanodeInfo chosenNode;
    //URL for TAIL 
    LocatedBlock lastBlk = blocks.get(blocks.size() - 1);
    try {
      chosenNode = JspHelper.bestNode(lastBlk);
    } catch (IOException e) {
      out.print(e.toString());
      dfs.close();
      return;
    }
    String fqdn = 
           InetAddress.getByName(chosenNode.getHost()).getCanonicalHostName();
    String tailUrl = "http://" + fqdn + ":" +
                     chosenNode.getInfoPort() + 
                 "/tail.jsp?filename=" + URLEncoder.encode(filename, "UTF-8") +
                 "&namenodeInfoPort=" + namenodeInfoPort +
                 "&chunkSizeToView=" + chunkSizeToView +
                 "&referrer=" + 
          URLEncoder.encode(req.getRequestURL() + "?" + req.getQueryString(),
                            "UTF-8");
    out.print("<a href=\"" + tailUrl + "\">Tail this file</a><br>");

    out.print("<form action=\"/browseBlock.jsp\" method=GET>");
    out.print("<b>Chunk size to view (in bytes, up to file's DFS block size): </b>");
    out.print("<input type=\"hidden\" name=\"blockId\" value=\"" + blockId +
              "\">");
    out.print("<input type=\"hidden\" name=\"blockSize\" value=\"" + 
              blockSize + "\">");
    out.print("<input type=\"hidden\" name=\"startOffset\" value=\"" + 
              startOffset + "\">");
    out.print("<input type=\"hidden\" name=\"filename\" value=\"" + filename +
              "\">");
    out.print("<input type=\"hidden\" name=\"datanodePort\" value=\"" + 
              datanodePort+ "\">");
    out.print("<input type=\"hidden\" name=\"namenodeInfoPort\" value=\"" +
              namenodeInfoPort + "\">");
    out.print("<input type=\"text\" name=\"chunkSizeToView\" value=" +
              chunkSizeToView + " size=10 maxlength=10>");
    out.print("&nbsp;&nbsp;<input type=\"submit\" name=\"submit\" value=\"Refresh\">");
    out.print("</form>");
    out.print("<hr>"); 
    out.print("<a name=\"blockDetails\"></a>");
    out.print("<B>Total number of blocks: "+blocks.size()+"</B><br>");
    //generate a table and dump the info
    out.println("\n<table>");
    for (LocatedBlock cur : blocks) {
      out.print("<tr>");
      final String blockidstring = Long.toString(cur.getBlock().getBlockId());
      blockSize = cur.getBlock().getNumBytes();
      out.print("<td>"+blockidstring+":</td>");
      DatanodeInfo[] locs = cur.getLocations();
      for(int j=0; j<locs.length; j++) {
        String datanodeAddr = locs[j].getName();
        datanodePort = Integer.parseInt(datanodeAddr.substring(
                                        datanodeAddr.indexOf(':') + 1, 
                                    datanodeAddr.length())); 
        fqdn = InetAddress.getByName(locs[j].getHost()).getCanonicalHostName();
        String blockUrl = "http://"+ fqdn + ":" +
                        locs[j].getInfoPort() +
                        "/browseBlock.jsp?blockId=" + blockidstring +
                        "&blockSize=" + blockSize +
               "&filename=" + URLEncoder.encode(filename, "UTF-8")+ 
                        "&datanodePort=" + datanodePort + 
                        "&genstamp=" + cur.getBlock().getGenerationStamp() + 
                        "&namenodeInfoPort=" + namenodeInfoPort +
                        "&chunkSizeToView=" + chunkSizeToView;
        out.print("<td>&nbsp</td>" 
          + "<td><a href=\"" + blockUrl + "\">" + datanodeAddr + "</a></td>");
      }
      out.println("</tr>");
    }
    out.println("</table>");
    out.print("<hr>");
    String namenodeHost = datanode.getNameNodeAddr().getHostName();
    out.print("<br><a href=\"http://" + 
              InetAddress.getByName(namenodeHost).getCanonicalHostName() + ":" +
              namenodeInfoPort + "/dfshealth.jsp\">Go back to DFS home</a>");
    dfs.close();
  }

  public void generateFileChunks(JspWriter out, HttpServletRequest req) 
    throws IOException {
    long startOffset = 0;
    int datanodePort = 0; 

    String namenodeInfoPortStr = req.getParameter("namenodeInfoPort");
    int namenodeInfoPort = -1;
    if (namenodeInfoPortStr != null)
      namenodeInfoPort = Integer.parseInt(namenodeInfoPortStr);

    final String filename = JspHelper.validatePath(
        req.getParameter("filename"));
    if (filename == null) {
      out.print("Invalid input (filename absent)");
      return;
    }
    
    final Long blockId = JspHelper.validateLong(req.getParameter("blockId"));
    if (blockId == null) {
      out.print("Invalid input (blockId absent)");
      return;
    }

    final DFSClient dfs = new DFSClient(datanode.getNameNodeAddr(), JspHelper.conf);
    
    AccessToken accessToken = AccessToken.DUMMY_TOKEN;
    if (JspHelper.conf
        .getBoolean(AccessTokenHandler.STRING_ENABLE_ACCESS_TOKEN, false)) {
      List<LocatedBlock> blks = dfs.namenode.getBlockLocations(filename, 0,
          Long.MAX_VALUE).getLocatedBlocks();
      if (blks == null || blks.size() == 0) {
        out.print("Can't locate file blocks");
        dfs.close();
        return;
      }
      for (int i = 0; i < blks.size(); i++) {
        if (blks.get(i).getBlock().getBlockId() == blockId) {
          accessToken = blks.get(i).getAccessToken();
          break;
        }
      }
    }
    
    final Long genStamp = JspHelper.validateLong(req.getParameter("genstamp"));
    if (genStamp == null) {
      out.print("Invalid input (genstamp absent)");
      return;
    }

    String blockSizeStr;
    long blockSize = 0;
    blockSizeStr = req.getParameter("blockSize"); 
    if (blockSizeStr == null) {
      out.print("Invalid input (blockSize absent)");
      return;
    }
    blockSize = Long.parseLong(blockSizeStr);
    
    final int chunkSizeToView = JspHelper.string2ChunkSizeToView(req.getParameter("chunkSizeToView"));

    String startOffsetStr = req.getParameter("startOffset");
    if (startOffsetStr == null || Long.parseLong(startOffsetStr) < 0)
      startOffset = 0;
    else startOffset = Long.parseLong(startOffsetStr);

    String datanodePortStr = req.getParameter("datanodePort");
    if (datanodePortStr == null) {
      out.print("Invalid input (datanodePort absent)");
      return;
    }
    datanodePort = Integer.parseInt(datanodePortStr);
    out.print("<h3>File: ");
    JspHelper.printPathWithLinks(filename, out, namenodeInfoPort);
    out.print("</h3><hr>");
    String parent = new File(filename).getParent();
    JspHelper.printGotoForm(out, namenodeInfoPort, parent);
    out.print("<hr>");
    out.print("<a href=\"http://" + req.getServerName() + ":" + 
              req.getServerPort() + 
              "/browseDirectory.jsp?dir=" + 
              URLEncoder.encode(parent, "UTF-8") +
              "&namenodeInfoPort=" + namenodeInfoPort + 
              "\"><i>Go back to dir listing</i></a><br>");
    out.print("<a href=\"#viewOptions\">Advanced view/download options</a><br>");
    out.print("<hr>");

    //Determine the prev & next blocks
    long nextStartOffset = 0;
    long nextBlockSize = 0;
    String nextBlockIdStr = null;
    String nextGenStamp = null;
    String nextHost = req.getServerName();
    int nextPort = req.getServerPort();
    int nextDatanodePort = datanodePort;
    //determine data for the next link
    if (startOffset + chunkSizeToView >= blockSize) {
      //we have to go to the next block from this point onwards
      List<LocatedBlock> blocks = 
        dfs.namenode.getBlockLocations(filename, 0, Long.MAX_VALUE).getLocatedBlocks();
      for (int i = 0; i < blocks.size(); i++) {
        if (blocks.get(i).getBlock().getBlockId() == blockId) {
          if (i != blocks.size() - 1) {
            LocatedBlock nextBlock = blocks.get(i+1);
            nextBlockIdStr = Long.toString(nextBlock.getBlock().getBlockId());
            nextGenStamp = Long.toString(nextBlock.getBlock().getGenerationStamp());
            nextStartOffset = 0;
            nextBlockSize = nextBlock.getBlock().getNumBytes();
            DatanodeInfo d = JspHelper.bestNode(nextBlock);
            String datanodeAddr = d.getName();
            nextDatanodePort = Integer.parseInt(
                                      datanodeAddr.substring(
                                           datanodeAddr.indexOf(':') + 1, 
                                      datanodeAddr.length())); 
            nextHost = InetAddress.getByName(d.getHost()).getCanonicalHostName();
            nextPort = d.getInfoPort(); 
          }
        }
      }
    } 
    else {
      //we are in the same block
      nextBlockIdStr = blockId.toString();
      nextStartOffset = startOffset + chunkSizeToView;
      nextBlockSize = blockSize;
      nextGenStamp = genStamp.toString();
    }
    String nextUrl = null;
    if (nextBlockIdStr != null) {
      nextUrl = "http://" + nextHost + ":" + 
                nextPort + 
                "/browseBlock.jsp?blockId=" + nextBlockIdStr +
                "&blockSize=" + nextBlockSize + "&startOffset=" + 
                nextStartOffset + 
                "&genstamp=" + nextGenStamp +
                "&filename=" + URLEncoder.encode(filename, "UTF-8") +
                "&chunkSizeToView=" + chunkSizeToView + 
                "&datanodePort=" + nextDatanodePort +
                "&namenodeInfoPort=" + namenodeInfoPort;
      out.print("<a href=\"" + nextUrl + "\">View Next chunk</a>&nbsp;&nbsp;");        
    }
    //determine data for the prev link
    String prevBlockIdStr = null;
    String prevGenStamp = null;
    long prevStartOffset = 0;
    long prevBlockSize = 0;
    String prevHost = req.getServerName();
    int prevPort = req.getServerPort();
    int prevDatanodePort = datanodePort;
    if (startOffset == 0) {
      List<LocatedBlock> blocks = 
        dfs.namenode.getBlockLocations(filename, 0, Long.MAX_VALUE).getLocatedBlocks();
      for (int i = 0; i < blocks.size(); i++) {
        if (blocks.get(i).getBlock().getBlockId() == blockId) {
          if (i != 0) {
            LocatedBlock prevBlock = blocks.get(i-1);
            prevBlockIdStr = Long.toString(prevBlock.getBlock().getBlockId());
            prevGenStamp = Long.toString(prevBlock.getBlock().getGenerationStamp());
            prevStartOffset = prevBlock.getBlock().getNumBytes() - chunkSizeToView;
            if (prevStartOffset < 0)
              prevStartOffset = 0;
            prevBlockSize = prevBlock.getBlock().getNumBytes();
            DatanodeInfo d = JspHelper.bestNode(prevBlock);
            String datanodeAddr = d.getName();
            prevDatanodePort = Integer.parseInt(
                                      datanodeAddr.substring(
                                          datanodeAddr.indexOf(':') + 1, 
                                      datanodeAddr.length())); 
            prevHost = InetAddress.getByName(d.getHost()).getCanonicalHostName();
            prevPort = d.getInfoPort();
          }
        }
      }
    }
    else {
      //we are in the same block
      prevBlockIdStr = blockId.toString();
      prevStartOffset = startOffset - chunkSizeToView;
      if (prevStartOffset < 0) prevStartOffset = 0;
      prevBlockSize = blockSize;
      prevGenStamp = genStamp.toString();
    }

    String prevUrl = null;
    if (prevBlockIdStr != null) {
      prevUrl = "http://" + prevHost + ":" + 
                prevPort + 
                "/browseBlock.jsp?blockId=" + prevBlockIdStr + 
                "&blockSize=" + prevBlockSize + "&startOffset=" + 
                prevStartOffset + 
                "&filename=" + URLEncoder.encode(filename, "UTF-8") + 
                "&chunkSizeToView=" + chunkSizeToView +
                "&genstamp=" + prevGenStamp +
                "&datanodePort=" + prevDatanodePort +
                "&namenodeInfoPort=" + namenodeInfoPort;
      out.print("<a href=\"" + prevUrl + "\">View Prev chunk</a>&nbsp;&nbsp;");
    }
    out.print("<hr>");
    out.print("<textarea cols=\"100\" rows=\"25\" wrap=\"virtual\" style=\"width:100%\" READONLY>");
    try {
    JspHelper.streamBlockInAscii(
            new InetSocketAddress(req.getServerName(), datanodePort), blockId, 
            accessToken, genStamp, blockSize, startOffset, chunkSizeToView, out);
    } catch (Exception e){
        out.print(e);
    }
    out.print("</textarea>");
    dfs.close();
  }

%>
<html>
<head>
<%JspHelper.createTitle(out, request, request.getParameter("filename")); %>
</head>
<body onload="document.goto.dir.focus()">
<% 
   generateFileChunks(out,request);
%>
<hr>
<% 
   generateFileDetails(out,request);
%>

<h2>Local logs</h2>
<a href="/logs/">Log</a> directory

<%
out.println(ServletUtil.htmlFooter());
%>
