begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Runtime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * This class repeatedly queries a namenode looking for corrupt replicas. If   * any are found a provided hadoop job is launched and the output printed  * to stdout.   *  * The syntax is:  *  * java BlockForensics http://[namenode]:[port]/corrupt_replicas_xml.jsp   *                    [sleep time between namenode query for corrupt blocks  *                      (in seconds)] [mapred jar location] [hdfs input path]  *  * All arguments are required.  */
end_comment

begin_class
DECL|class|BlockForensics
specifier|public
class|class
name|BlockForensics
block|{
DECL|method|join (List<?> l, String sep)
specifier|public
specifier|static
name|String
name|join
parameter_list|(
name|List
argument_list|<
name|?
argument_list|>
name|l
parameter_list|,
name|String
name|sep
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
name|it
init|=
name|l
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// runs hadoop command and prints output to stdout
DECL|method|runHadoopCmd (String .... args)
specifier|public
specifier|static
name|void
name|runHadoopCmd
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|hadoop_home
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"HADOOP_PREFIX"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
literal|"bin/hadoop"
argument_list|)
expr_stmt|;
name|l
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|l
argument_list|)
decl_stmt|;
if|if
condition|(
name|hadoop_home
operator|!=
literal|null
condition|)
block|{
name|pb
operator|.
name|directory
argument_list|(
operator|new
name|File
argument_list|(
name|hadoop_home
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pb
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Process
name|p
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|p
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|SAXException
throws|,
name|ParserConfigurationException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
if|if
condition|(
name|System
operator|.
name|getenv
argument_list|(
literal|"HADOOP_PREFIX"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"The environmental variable HADOOP_PREFIX is undefined"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|4
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Usage: java BlockForensics [http://namenode:port/"
operator|+
literal|"corrupt_replicas_xml.jsp] [sleep time between "
operator|+
literal|"requests (in milliseconds)] [mapred jar location] "
operator|+
literal|"[hdfs input path]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|sleepTime
init|=
literal|30000
decl_stmt|;
try|try
block|{
name|sleepTime
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The sleep time entered is invalid, "
operator|+
literal|"using default value: "
operator|+
name|sleepTime
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|Long
argument_list|>
name|blockIds
init|=
operator|new
name|TreeSet
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|InputStream
name|xml
init|=
operator|new
name|URL
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
operator|.
name|openConnection
argument_list|()
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|DocumentBuilderFactory
name|fact
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
name|fact
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|NodeList
name|corruptReplicaNodes
init|=
name|doc
operator|.
name|getElementsByTagName
argument_list|(
literal|"block_id"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|searchBlockIds
init|=
operator|new
name|LinkedList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|corruptReplicaNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Long
name|blockId
init|=
operator|new
name|Long
argument_list|(
name|corruptReplicaNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|blockIds
operator|.
name|contains
argument_list|(
name|blockId
argument_list|)
condition|)
block|{
name|blockIds
operator|.
name|add
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
name|searchBlockIds
operator|.
name|add
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|searchBlockIds
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|blockIdsStr
init|=
name|BlockForensics
operator|.
name|join
argument_list|(
name|searchBlockIds
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nSearching for: "
operator|+
name|blockIdsStr
argument_list|)
expr_stmt|;
name|String
name|tmpDir
init|=
operator|new
name|String
argument_list|(
literal|"/tmp-block-forensics-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using temporary dir: "
operator|+
name|tmpDir
argument_list|)
expr_stmt|;
comment|// delete tmp dir
name|BlockForensics
operator|.
name|runHadoopCmd
argument_list|(
literal|"fs"
argument_list|,
literal|"-rmr"
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
comment|// launch mapred job
name|BlockForensics
operator|.
name|runHadoopCmd
argument_list|(
literal|"jar"
argument_list|,
name|args
index|[
literal|2
index|]
argument_list|,
comment|// jar location
name|args
index|[
literal|3
index|]
argument_list|,
comment|// input dir
name|tmpDir
argument_list|,
comment|// output dir
name|blockIdsStr
comment|// comma delimited list of blocks
argument_list|)
expr_stmt|;
comment|// cat output
name|BlockForensics
operator|.
name|runHadoopCmd
argument_list|(
literal|"fs"
argument_list|,
literal|"-cat"
argument_list|,
name|tmpDir
operator|+
literal|"/part*"
argument_list|)
expr_stmt|;
comment|// delete temp dir
name|BlockForensics
operator|.
name|runHadoopCmd
argument_list|(
literal|"fs"
argument_list|,
literal|"-rmr"
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
name|int
name|sleepSecs
init|=
call|(
name|int
call|)
argument_list|(
name|sleepTime
operator|/
literal|1000.
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Sleeping for "
operator|+
name|sleepSecs
operator|+
literal|" second"
operator|+
operator|(
name|sleepSecs
operator|==
literal|1
condition|?
literal|""
else|:
literal|"s"
operator|)
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

