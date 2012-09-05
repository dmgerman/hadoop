begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|DigestInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|lang
operator|.
name|Math
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletOutputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
operator|.
name|HttpConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|SecurityUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|HdfsConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|StorageErrorReporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|Storage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NNStorage
operator|.
name|NameNodeDirType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|RemoteEditLog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
operator|.
name|DataTransferThrottler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|MD5Hash
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * This class provides fetching a specified file from the NameNode.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TransferFsImage
specifier|public
class|class
name|TransferFsImage
block|{
DECL|field|CONTENT_LENGTH
specifier|public
specifier|final
specifier|static
name|String
name|CONTENT_LENGTH
init|=
literal|"Content-Length"
decl_stmt|;
DECL|field|MD5_HEADER
specifier|public
specifier|final
specifier|static
name|String
name|MD5_HEADER
init|=
literal|"X-MD5-Digest"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|timeout
specifier|static
name|int
name|timeout
init|=
literal|0
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TransferFsImage
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|downloadMostRecentImageToDirectory (String fsName, File dir)
specifier|public
specifier|static
name|void
name|downloadMostRecentImageToDirectory
parameter_list|(
name|String
name|fsName
parameter_list|,
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileId
init|=
name|GetImageServlet
operator|.
name|getParamStringForMostRecentImage
argument_list|()
decl_stmt|;
name|getFileClient
argument_list|(
name|fsName
argument_list|,
name|fileId
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|dir
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|downloadImageToStorage ( String fsName, long imageTxId, Storage dstStorage, boolean needDigest)
specifier|public
specifier|static
name|MD5Hash
name|downloadImageToStorage
parameter_list|(
name|String
name|fsName
parameter_list|,
name|long
name|imageTxId
parameter_list|,
name|Storage
name|dstStorage
parameter_list|,
name|boolean
name|needDigest
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileid
init|=
name|GetImageServlet
operator|.
name|getParamStringForImage
argument_list|(
name|imageTxId
argument_list|,
name|dstStorage
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
name|NNStorage
operator|.
name|getCheckpointImageFileName
argument_list|(
name|imageTxId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|dstFiles
init|=
name|dstStorage
operator|.
name|getFiles
argument_list|(
name|NameNodeDirType
operator|.
name|IMAGE
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|dstFiles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No targets in destination storage!"
argument_list|)
throw|;
block|}
name|MD5Hash
name|hash
init|=
name|getFileClient
argument_list|(
name|fsName
argument_list|,
name|fileid
argument_list|,
name|dstFiles
argument_list|,
name|dstStorage
argument_list|,
name|needDigest
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Downloaded file "
operator|+
name|dstFiles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
operator|+
literal|" size "
operator|+
name|dstFiles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|length
argument_list|()
operator|+
literal|" bytes."
argument_list|)
expr_stmt|;
return|return
name|hash
return|;
block|}
DECL|method|downloadEditsToStorage (String fsName, RemoteEditLog log, NNStorage dstStorage)
specifier|static
name|void
name|downloadEditsToStorage
parameter_list|(
name|String
name|fsName
parameter_list|,
name|RemoteEditLog
name|log
parameter_list|,
name|NNStorage
name|dstStorage
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|log
operator|.
name|getStartTxId
argument_list|()
operator|>
literal|0
operator|&&
name|log
operator|.
name|getEndTxId
argument_list|()
operator|>
literal|0
operator|:
literal|"bad log: "
operator|+
name|log
assert|;
name|String
name|fileid
init|=
name|GetImageServlet
operator|.
name|getParamStringForLog
argument_list|(
name|log
argument_list|,
name|dstStorage
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
name|log
operator|.
name|getStartTxId
argument_list|()
argument_list|,
name|log
operator|.
name|getEndTxId
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|dstFiles
init|=
name|dstStorage
operator|.
name|getFiles
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|dstFiles
operator|.
name|isEmpty
argument_list|()
operator|:
literal|"No checkpoint targets."
assert|;
for|for
control|(
name|File
name|f
range|:
name|dstFiles
control|)
block|{
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
operator|&&
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipping download of remote edit log "
operator|+
name|log
operator|+
literal|" since it already is stored locally at "
operator|+
name|f
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Dest file: "
operator|+
name|f
argument_list|)
expr_stmt|;
block|}
block|}
name|getFileClient
argument_list|(
name|fsName
argument_list|,
name|fileid
argument_list|,
name|dstFiles
argument_list|,
name|dstStorage
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Downloaded file "
operator|+
name|dstFiles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
operator|+
literal|" size "
operator|+
name|dstFiles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|length
argument_list|()
operator|+
literal|" bytes."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Requests that the NameNode download an image from this node.    *    * @param fsName the http address for the remote NN    * @param imageListenAddress the host/port where the local node is running an    *                           HTTPServer hosting GetImageServlet    * @param storage the storage directory to transfer the image from    * @param txid the transaction ID of the image to be uploaded    */
DECL|method|uploadImageFromStorage (String fsName, InetSocketAddress imageListenAddress, Storage storage, long txid)
specifier|public
specifier|static
name|void
name|uploadImageFromStorage
parameter_list|(
name|String
name|fsName
parameter_list|,
name|InetSocketAddress
name|imageListenAddress
parameter_list|,
name|Storage
name|storage
parameter_list|,
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileid
init|=
name|GetImageServlet
operator|.
name|getParamStringToPutImage
argument_list|(
name|txid
argument_list|,
name|imageListenAddress
argument_list|,
name|storage
argument_list|)
decl_stmt|;
comment|// this doesn't directly upload an image, but rather asks the NN
comment|// to connect back to the 2NN to download the specified image.
try|try
block|{
name|TransferFsImage
operator|.
name|getFileClient
argument_list|(
name|fsName
argument_list|,
name|fileid
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpGetFailedException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getResponseCode
argument_list|()
operator|==
name|HttpServletResponse
operator|.
name|SC_CONFLICT
condition|)
block|{
comment|// this is OK - this means that a previous attempt to upload
comment|// this checkpoint succeeded even though we thought it failed.
name|LOG
operator|.
name|info
argument_list|(
literal|"Image upload with txid "
operator|+
name|txid
operator|+
literal|" conflicted with a previous image upload to the "
operator|+
literal|"same NameNode. Continuing..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Uploaded image with txid "
operator|+
name|txid
operator|+
literal|" to namenode at "
operator|+
name|fsName
argument_list|)
expr_stmt|;
block|}
comment|/**    * A server-side method to respond to a getfile http request    * Copies the contents of the local file into the output stream.    */
DECL|method|getFileServer (ServletResponse response, File localfile, FileInputStream infile, DataTransferThrottler throttler)
specifier|public
specifier|static
name|void
name|getFileServer
parameter_list|(
name|ServletResponse
name|response
parameter_list|,
name|File
name|localfile
parameter_list|,
name|FileInputStream
name|infile
parameter_list|,
name|DataTransferThrottler
name|throttler
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|HdfsConstants
operator|.
name|IO_FILE_BUFFER_SIZE
index|]
decl_stmt|;
name|ServletOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CheckpointFaultInjector
operator|.
name|getInstance
argument_list|()
operator|.
name|aboutToSendFile
argument_list|(
name|localfile
argument_list|)
expr_stmt|;
name|out
operator|=
name|response
operator|.
name|getOutputStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|CheckpointFaultInjector
operator|.
name|getInstance
argument_list|()
operator|.
name|shouldSendShortFile
argument_list|(
name|localfile
argument_list|)
condition|)
block|{
comment|// Test sending image shorter than localfile
name|long
name|len
init|=
name|localfile
operator|.
name|length
argument_list|()
decl_stmt|;
name|buf
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|len
operator|/
literal|2
argument_list|,
name|HdfsConstants
operator|.
name|IO_FILE_BUFFER_SIZE
argument_list|)
index|]
expr_stmt|;
comment|// This will read at most half of the image
comment|// and the rest of the image will be sent over the wire
name|infile
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
name|int
name|num
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|num
operator|>
literal|0
condition|)
block|{
name|num
operator|=
name|infile
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
if|if
condition|(
name|num
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|CheckpointFaultInjector
operator|.
name|getInstance
argument_list|()
operator|.
name|shouldCorruptAByte
argument_list|(
name|localfile
argument_list|)
condition|)
block|{
comment|// Simulate a corrupted byte on the wire
name|LOG
operator|.
name|warn
argument_list|(
literal|"SIMULATING A CORRUPT BYTE IN IMAGE TRANSFER!"
argument_list|)
expr_stmt|;
name|buf
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|num
argument_list|)
expr_stmt|;
if|if
condition|(
name|throttler
operator|!=
literal|null
condition|)
block|{
name|throttler
operator|.
name|throttle
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Client-side Method to fetch file from a server    * Copies the response from the URL to a list of local files.    * @param dstStorage if an error occurs writing to one of the files,    *                   this storage object will be notified.     * @Return a digest of the received file if getChecksum is true    */
DECL|method|getFileClient (String nnHostPort, String queryString, List<File> localPaths, Storage dstStorage, boolean getChecksum)
specifier|static
name|MD5Hash
name|getFileClient
parameter_list|(
name|String
name|nnHostPort
parameter_list|,
name|String
name|queryString
parameter_list|,
name|List
argument_list|<
name|File
argument_list|>
name|localPaths
parameter_list|,
name|Storage
name|dstStorage
parameter_list|,
name|boolean
name|getChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|str
init|=
name|HttpConfig
operator|.
name|getSchemePrefix
argument_list|()
operator|+
name|nnHostPort
operator|+
literal|"/getimage?"
operator|+
name|queryString
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Opening connection to "
operator|+
name|str
argument_list|)
expr_stmt|;
comment|//
comment|// open connection to remote server
comment|//
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|str
argument_list|)
decl_stmt|;
return|return
name|doGetUrl
argument_list|(
name|url
argument_list|,
name|localPaths
argument_list|,
name|dstStorage
argument_list|,
name|getChecksum
argument_list|)
return|;
block|}
DECL|method|doGetUrl (URL url, List<File> localPaths, Storage dstStorage, boolean getChecksum)
specifier|public
specifier|static
name|MD5Hash
name|doGetUrl
parameter_list|(
name|URL
name|url
parameter_list|,
name|List
argument_list|<
name|File
argument_list|>
name|localPaths
parameter_list|,
name|Storage
name|dstStorage
parameter_list|,
name|boolean
name|getChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|SecurityUtil
operator|.
name|openSecureHttpConnection
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
block|{
comment|// Set the ping interval as timeout
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|timeout
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_TRANSFER_TIMEOUT_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_TRANSFER_TIMEOUT_DEFAULT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|timeout
operator|>
literal|0
condition|)
block|{
name|connection
operator|.
name|setConnectTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setReadTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|.
name|getResponseCode
argument_list|()
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
throw|throw
operator|new
name|HttpGetFailedException
argument_list|(
literal|"Image transfer servlet at "
operator|+
name|url
operator|+
literal|" failed with status code "
operator|+
name|connection
operator|.
name|getResponseCode
argument_list|()
operator|+
literal|"\nResponse message:\n"
operator|+
name|connection
operator|.
name|getResponseMessage
argument_list|()
argument_list|,
name|connection
argument_list|)
throw|;
block|}
name|long
name|advertisedSize
decl_stmt|;
name|String
name|contentLength
init|=
name|connection
operator|.
name|getHeaderField
argument_list|(
name|CONTENT_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentLength
operator|!=
literal|null
condition|)
block|{
name|advertisedSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|contentLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|CONTENT_LENGTH
operator|+
literal|" header is not provided "
operator|+
literal|"by the namenode when trying to fetch "
operator|+
name|url
argument_list|)
throw|;
block|}
if|if
condition|(
name|localPaths
operator|!=
literal|null
condition|)
block|{
name|String
name|fsImageName
init|=
name|connection
operator|.
name|getHeaderField
argument_list|(
name|GetImageServlet
operator|.
name|HADOOP_IMAGE_EDITS_HEADER
argument_list|)
decl_stmt|;
comment|// If the local paths refer to directories, use the server-provided header
comment|// as the filename within that directory
name|List
argument_list|<
name|File
argument_list|>
name|newLocalPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|localPath
range|:
name|localPaths
control|)
block|{
if|if
condition|(
name|localPath
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|fsImageName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No filename header provided by server"
argument_list|)
throw|;
block|}
name|newLocalPaths
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|localPath
argument_list|,
name|fsImageName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newLocalPaths
operator|.
name|add
argument_list|(
name|localPath
argument_list|)
expr_stmt|;
block|}
block|}
name|localPaths
operator|=
name|newLocalPaths
expr_stmt|;
block|}
name|MD5Hash
name|advertisedDigest
init|=
name|parseMD5Header
argument_list|(
name|connection
argument_list|)
decl_stmt|;
name|long
name|received
init|=
literal|0
decl_stmt|;
name|InputStream
name|stream
init|=
name|connection
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|MessageDigest
name|digester
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getChecksum
condition|)
block|{
name|digester
operator|=
name|MD5Hash
operator|.
name|getDigester
argument_list|()
expr_stmt|;
name|stream
operator|=
operator|new
name|DigestInputStream
argument_list|(
name|stream
argument_list|,
name|digester
argument_list|)
expr_stmt|;
block|}
name|boolean
name|finishedReceiving
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|FileOutputStream
argument_list|>
name|outputStreams
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|localPaths
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|f
range|:
name|localPaths
control|)
block|{
try|try
block|{
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Overwriting existing file "
operator|+
name|f
operator|+
literal|" with file downloaded from "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
name|outputStreams
operator|.
name|add
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to download file "
operator|+
name|f
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
comment|// This will be null if we're downloading the fsimage to a file
comment|// outside of an NNStorage directory.
if|if
condition|(
name|dstStorage
operator|!=
literal|null
operator|&&
operator|(
name|dstStorage
operator|instanceof
name|StorageErrorReporter
operator|)
condition|)
block|{
operator|(
operator|(
name|StorageErrorReporter
operator|)
name|dstStorage
operator|)
operator|.
name|reportErrorOnFile
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|outputStreams
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to download to any storage directory"
argument_list|)
throw|;
block|}
block|}
name|int
name|num
init|=
literal|1
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|HdfsConstants
operator|.
name|IO_FILE_BUFFER_SIZE
index|]
decl_stmt|;
while|while
condition|(
name|num
operator|>
literal|0
condition|)
block|{
name|num
operator|=
name|stream
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
if|if
condition|(
name|num
operator|>
literal|0
condition|)
block|{
name|received
operator|+=
name|num
expr_stmt|;
for|for
control|(
name|FileOutputStream
name|fos
range|:
name|outputStreams
control|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|finishedReceiving
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|FileOutputStream
name|fos
range|:
name|outputStreams
control|)
block|{
name|fos
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|finishedReceiving
operator|&&
name|received
operator|!=
name|advertisedSize
condition|)
block|{
comment|// only throw this exception if we think we read all of it on our end
comment|// -- otherwise a client-side IOException would be masked by this
comment|// exception that makes it look like a server-side problem!
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File "
operator|+
name|url
operator|+
literal|" received length "
operator|+
name|received
operator|+
literal|" is not of the advertised size "
operator|+
name|advertisedSize
argument_list|)
throw|;
block|}
block|}
name|double
name|xferSec
init|=
name|Math
operator|.
name|max
argument_list|(
operator|(
call|(
name|float
call|)
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
argument_list|)
operator|)
operator|/
literal|1000.0
argument_list|,
literal|0.001
argument_list|)
decl_stmt|;
name|long
name|xferKb
init|=
name|received
operator|/
literal|1024
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Transfer took %.2fs at %.2f KB/s"
argument_list|,
name|xferSec
argument_list|,
name|xferKb
operator|/
name|xferSec
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|digester
operator|!=
literal|null
condition|)
block|{
name|MD5Hash
name|computedDigest
init|=
operator|new
name|MD5Hash
argument_list|(
name|digester
operator|.
name|digest
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|advertisedDigest
operator|!=
literal|null
operator|&&
operator|!
name|computedDigest
operator|.
name|equals
argument_list|(
name|advertisedDigest
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File "
operator|+
name|url
operator|+
literal|" computed digest "
operator|+
name|computedDigest
operator|+
literal|" does not match advertised digest "
operator|+
name|advertisedDigest
argument_list|)
throw|;
block|}
return|return
name|computedDigest
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|parseMD5Header (HttpURLConnection connection)
specifier|private
specifier|static
name|MD5Hash
name|parseMD5Header
parameter_list|(
name|HttpURLConnection
name|connection
parameter_list|)
block|{
name|String
name|header
init|=
name|connection
operator|.
name|getHeaderField
argument_list|(
name|MD5_HEADER
argument_list|)
decl_stmt|;
return|return
operator|(
name|header
operator|!=
literal|null
operator|)
condition|?
operator|new
name|MD5Hash
argument_list|(
name|header
argument_list|)
else|:
literal|null
return|;
block|}
DECL|class|HttpGetFailedException
specifier|public
specifier|static
class|class
name|HttpGetFailedException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|responseCode
specifier|private
specifier|final
name|int
name|responseCode
decl_stmt|;
DECL|method|HttpGetFailedException (String msg, HttpURLConnection connection)
name|HttpGetFailedException
parameter_list|(
name|String
name|msg
parameter_list|,
name|HttpURLConnection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|this
operator|.
name|responseCode
operator|=
name|connection
operator|.
name|getResponseCode
argument_list|()
expr_stmt|;
block|}
DECL|method|getResponseCode ()
specifier|public
name|int
name|getResponseCode
parameter_list|()
block|{
return|return
name|responseCode
return|;
block|}
block|}
block|}
end_class

end_unit

