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
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServlet
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
name|HttpServletRequest
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
name|net
operator|.
name|NetUtils
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
name|hdfs
operator|.
name|DFSUtil
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
name|HAUtil
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
name|JspHelper
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
name|common
operator|.
name|StorageInfo
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
name|hdfs
operator|.
name|util
operator|.
name|MD5FileUtils
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
name|HttpServer
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
name|IOUtils
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
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
name|ServletUtil
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
name|StringUtils
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
name|base
operator|.
name|Preconditions
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
name|net
operator|.
name|InetAddresses
import|;
end_import

begin_comment
comment|/**  * This class is used in Namesystem's jetty to retrieve a file.  * Typically used by the Secondary NameNode to retrieve image and  * edit file for periodic checkpointing.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|GetImageServlet
specifier|public
class|class
name|GetImageServlet
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7669068179452648952L
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
name|GetImageServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONTENT_DISPOSITION
specifier|public
specifier|final
specifier|static
name|String
name|CONTENT_DISPOSITION
init|=
literal|"Content-Disposition"
decl_stmt|;
DECL|field|HADOOP_IMAGE_EDITS_HEADER
specifier|public
specifier|final
specifier|static
name|String
name|HADOOP_IMAGE_EDITS_HEADER
init|=
literal|"X-Image-Edits-Name"
decl_stmt|;
DECL|field|TXID_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|TXID_PARAM
init|=
literal|"txid"
decl_stmt|;
DECL|field|START_TXID_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|START_TXID_PARAM
init|=
literal|"startTxId"
decl_stmt|;
DECL|field|END_TXID_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|END_TXID_PARAM
init|=
literal|"endTxId"
decl_stmt|;
DECL|field|STORAGEINFO_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|STORAGEINFO_PARAM
init|=
literal|"storageInfo"
decl_stmt|;
DECL|field|LATEST_FSIMAGE_VALUE
specifier|private
specifier|static
specifier|final
name|String
name|LATEST_FSIMAGE_VALUE
init|=
literal|"latest"
decl_stmt|;
DECL|field|currentlyDownloadingCheckpoints
specifier|private
specifier|static
name|Set
argument_list|<
name|Long
argument_list|>
name|currentlyDownloadingCheckpoints
init|=
name|Collections
operator|.
expr|<
name|Long
operator|>
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (final HttpServletRequest request, final HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
name|ServletContext
name|context
init|=
name|getServletContext
argument_list|()
decl_stmt|;
specifier|final
name|FSImage
name|nnImage
init|=
name|NameNodeHttpServer
operator|.
name|getFsImageFromContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|GetImageParams
name|parsedParams
init|=
operator|new
name|GetImageParams
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|(
name|Configuration
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|)
decl_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
operator|&&
operator|!
name|isValidRequestor
argument_list|(
name|context
argument_list|,
name|request
operator|.
name|getUserPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|conf
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
literal|"Only Namenode, Secondary Namenode, and administrators may access "
operator|+
literal|"this servlet"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Received non-NN/SNN/administrator request for image or edits from "
operator|+
name|request
operator|.
name|getUserPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" at "
operator|+
name|request
operator|.
name|getRemoteHost
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|myStorageInfoString
init|=
name|nnImage
operator|.
name|getStorage
argument_list|()
operator|.
name|toColonSeparatedString
argument_list|()
decl_stmt|;
name|String
name|theirStorageInfoString
init|=
name|parsedParams
operator|.
name|getStorageInfoString
argument_list|()
decl_stmt|;
if|if
condition|(
name|theirStorageInfoString
operator|!=
literal|null
operator|&&
operator|!
name|myStorageInfoString
operator|.
name|equals
argument_list|(
name|theirStorageInfoString
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
literal|"This namenode has storage info "
operator|+
name|myStorageInfoString
operator|+
literal|" but the secondary expected "
operator|+
name|theirStorageInfoString
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Received an invalid request file transfer request "
operator|+
literal|"from a secondary with storage info "
operator|+
name|theirStorageInfoString
argument_list|)
expr_stmt|;
return|return;
block|}
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|parsedParams
operator|.
name|isGetImage
argument_list|()
condition|)
block|{
name|long
name|txid
init|=
name|parsedParams
operator|.
name|getTxId
argument_list|()
decl_stmt|;
name|File
name|imageFile
init|=
literal|null
decl_stmt|;
name|String
name|errorMessage
init|=
literal|"Could not find image"
decl_stmt|;
if|if
condition|(
name|parsedParams
operator|.
name|shouldFetchLatest
argument_list|()
condition|)
block|{
name|imageFile
operator|=
name|nnImage
operator|.
name|getStorage
argument_list|()
operator|.
name|getHighestFsImageName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|errorMessage
operator|+=
literal|" with txid "
operator|+
name|txid
expr_stmt|;
name|imageFile
operator|=
name|nnImage
operator|.
name|getStorage
argument_list|()
operator|.
name|getFsImageName
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|imageFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|errorMessage
argument_list|)
throw|;
block|}
name|CheckpointFaultInjector
operator|.
name|getInstance
argument_list|()
operator|.
name|beforeGetImageSetsHeaders
argument_list|()
expr_stmt|;
name|serveFile
argument_list|(
name|imageFile
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parsedParams
operator|.
name|isGetEdit
argument_list|()
condition|)
block|{
name|long
name|startTxId
init|=
name|parsedParams
operator|.
name|getStartTxId
argument_list|()
decl_stmt|;
name|long
name|endTxId
init|=
name|parsedParams
operator|.
name|getEndTxId
argument_list|()
decl_stmt|;
name|File
name|editFile
init|=
name|nnImage
operator|.
name|getStorage
argument_list|()
operator|.
name|findFinalizedEditsFile
argument_list|(
name|startTxId
argument_list|,
name|endTxId
argument_list|)
decl_stmt|;
name|serveFile
argument_list|(
name|editFile
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parsedParams
operator|.
name|isPutImage
argument_list|()
condition|)
block|{
specifier|final
name|long
name|txid
init|=
name|parsedParams
operator|.
name|getTxId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|currentlyDownloadingCheckpoints
operator|.
name|add
argument_list|(
name|txid
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_CONFLICT
argument_list|,
literal|"Another checkpointer is already in the process of uploading a"
operator|+
literal|" checkpoint made at transaction ID "
operator|+
name|txid
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
try|try
block|{
if|if
condition|(
name|nnImage
operator|.
name|getStorage
argument_list|()
operator|.
name|findImageFile
argument_list|(
name|txid
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_CONFLICT
argument_list|,
literal|"Another checkpointer already uploaded an checkpoint "
operator|+
literal|"for txid "
operator|+
name|txid
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// We may have lost our ticket since last checkpoint, log in again, just in case
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
block|}
comment|// issue a HTTP get request to download the new fsimage
name|MD5Hash
name|downloadImageDigest
init|=
name|TransferFsImage
operator|.
name|downloadImageToStorage
argument_list|(
name|parsedParams
operator|.
name|getInfoServer
argument_list|()
argument_list|,
name|txid
argument_list|,
name|nnImage
operator|.
name|getStorage
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|nnImage
operator|.
name|saveDigestAndRenameCheckpointImage
argument_list|(
name|txid
argument_list|,
name|downloadImageDigest
argument_list|)
expr_stmt|;
comment|// Now that we have a new checkpoint, we might be able to
comment|// remove some old ones.
name|nnImage
operator|.
name|purgeOldStorage
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|currentlyDownloadingCheckpoints
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|serveFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|setVerificationHeaders
argument_list|(
name|response
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|setFileNameHeaders
argument_list|(
name|response
argument_list|,
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// Potential race where the file was deleted while we were in the
comment|// process of setting headers!
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
comment|// It's possible the file could be deleted after this point, but
comment|// we've already opened the 'fis' stream.
comment|// It's also possible length could change, but this would be
comment|// detected by the client side as an inaccurate length header.
block|}
comment|// send file
name|TransferFsImage
operator|.
name|getFileServer
argument_list|(
name|response
argument_list|,
name|file
argument_list|,
name|fis
argument_list|,
name|getThrottler
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fis
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|String
name|errMsg
init|=
literal|"GetImage failed. "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_GONE
argument_list|,
name|errMsg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|errMsg
argument_list|)
throw|;
block|}
finally|finally
block|{
name|response
operator|.
name|getOutputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setFileNameHeaders (HttpServletResponse response, File file)
specifier|public
specifier|static
name|void
name|setFileNameHeaders
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|,
name|File
name|file
parameter_list|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
name|CONTENT_DISPOSITION
argument_list|,
literal|"attachment; filename="
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
name|HADOOP_IMAGE_EDITS_HEADER
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a throttler from conf    * @param conf configuration    * @return a data transfer throttler    */
DECL|method|getThrottler (Configuration conf)
specifier|public
specifier|final
specifier|static
name|DataTransferThrottler
name|getThrottler
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|long
name|transferBandwidth
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_TRANSFER_RATE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_TRANSFER_RATE_DEFAULT
argument_list|)
decl_stmt|;
name|DataTransferThrottler
name|throttler
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|transferBandwidth
operator|>
literal|0
condition|)
block|{
name|throttler
operator|=
operator|new
name|DataTransferThrottler
argument_list|(
name|transferBandwidth
argument_list|)
expr_stmt|;
block|}
return|return
name|throttler
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|isValidRequestor (ServletContext context, String remoteUser, Configuration conf)
specifier|static
name|boolean
name|isValidRequestor
parameter_list|(
name|ServletContext
name|context
parameter_list|,
name|String
name|remoteUser
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|remoteUser
operator|==
literal|null
condition|)
block|{
comment|// This really shouldn't happen...
name|LOG
operator|.
name|warn
argument_list|(
literal|"Received null remoteUser while authorizing access to getImage servlet"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|validRequestors
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|validRequestors
operator|.
name|add
argument_list|(
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|)
argument_list|,
name|NameNode
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|validRequestors
operator|.
name|add
argument_list|(
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SECONDARY_NAMENODE_USER_NAME_KEY
argument_list|)
argument_list|,
name|SecondaryNameNode
operator|.
name|getHttpAddress
argument_list|(
name|conf
argument_list|)
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|conf
argument_list|,
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
argument_list|)
condition|)
block|{
name|Configuration
name|otherNnConf
init|=
name|HAUtil
operator|.
name|getConfForOtherNode
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|validRequestors
operator|.
name|add
argument_list|(
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|otherNnConf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|)
argument_list|,
name|NameNode
operator|.
name|getAddress
argument_list|(
name|otherNnConf
argument_list|)
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|v
range|:
name|validRequestors
control|)
block|{
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|equals
argument_list|(
name|remoteUser
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"GetImageServlet allowing checkpointer: "
operator|+
name|remoteUser
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
name|HttpServer
operator|.
name|userHasAdministratorAccess
argument_list|(
name|context
argument_list|,
name|remoteUser
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"GetImageServlet allowing administrator: "
operator|+
name|remoteUser
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"GetImageServlet rejecting: "
operator|+
name|remoteUser
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|/**    * Set headers for content length, and, if available, md5.    * @throws IOException     */
DECL|method|setVerificationHeaders (HttpServletResponse response, File file)
specifier|public
specifier|static
name|void
name|setVerificationHeaders
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setHeader
argument_list|(
name|TransferFsImage
operator|.
name|CONTENT_LENGTH
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|MD5Hash
name|hash
init|=
name|MD5FileUtils
operator|.
name|readStoredMd5ForFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|hash
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
name|TransferFsImage
operator|.
name|MD5_HEADER
argument_list|,
name|hash
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getParamStringForMostRecentImage ()
specifier|static
name|String
name|getParamStringForMostRecentImage
parameter_list|()
block|{
return|return
literal|"getimage=1&"
operator|+
name|TXID_PARAM
operator|+
literal|"="
operator|+
name|LATEST_FSIMAGE_VALUE
return|;
block|}
DECL|method|getParamStringForImage (long txid, StorageInfo remoteStorageInfo)
specifier|static
name|String
name|getParamStringForImage
parameter_list|(
name|long
name|txid
parameter_list|,
name|StorageInfo
name|remoteStorageInfo
parameter_list|)
block|{
return|return
literal|"getimage=1&"
operator|+
name|TXID_PARAM
operator|+
literal|"="
operator|+
name|txid
operator|+
literal|"&"
operator|+
name|STORAGEINFO_PARAM
operator|+
literal|"="
operator|+
name|remoteStorageInfo
operator|.
name|toColonSeparatedString
argument_list|()
return|;
block|}
DECL|method|getParamStringForLog (RemoteEditLog log, StorageInfo remoteStorageInfo)
specifier|static
name|String
name|getParamStringForLog
parameter_list|(
name|RemoteEditLog
name|log
parameter_list|,
name|StorageInfo
name|remoteStorageInfo
parameter_list|)
block|{
return|return
literal|"getedit=1&"
operator|+
name|START_TXID_PARAM
operator|+
literal|"="
operator|+
name|log
operator|.
name|getStartTxId
argument_list|()
operator|+
literal|"&"
operator|+
name|END_TXID_PARAM
operator|+
literal|"="
operator|+
name|log
operator|.
name|getEndTxId
argument_list|()
operator|+
literal|"&"
operator|+
name|STORAGEINFO_PARAM
operator|+
literal|"="
operator|+
name|remoteStorageInfo
operator|.
name|toColonSeparatedString
argument_list|()
return|;
block|}
DECL|method|getParamStringToPutImage (long txid, InetSocketAddress imageListenAddress, Storage storage)
specifier|static
name|String
name|getParamStringToPutImage
parameter_list|(
name|long
name|txid
parameter_list|,
name|InetSocketAddress
name|imageListenAddress
parameter_list|,
name|Storage
name|storage
parameter_list|)
block|{
name|String
name|machine
init|=
operator|!
name|imageListenAddress
operator|.
name|isUnresolved
argument_list|()
operator|&&
name|imageListenAddress
operator|.
name|getAddress
argument_list|()
operator|.
name|isAnyLocalAddress
argument_list|()
condition|?
literal|null
else|:
name|imageListenAddress
operator|.
name|getHostName
argument_list|()
decl_stmt|;
return|return
literal|"putimage=1"
operator|+
literal|"&"
operator|+
name|TXID_PARAM
operator|+
literal|"="
operator|+
name|txid
operator|+
literal|"&port="
operator|+
name|imageListenAddress
operator|.
name|getPort
argument_list|()
operator|+
operator|(
name|machine
operator|!=
literal|null
condition|?
literal|"&machine="
operator|+
name|machine
else|:
literal|""
operator|)
operator|+
literal|"&"
operator|+
name|STORAGEINFO_PARAM
operator|+
literal|"="
operator|+
name|storage
operator|.
name|toColonSeparatedString
argument_list|()
return|;
block|}
DECL|class|GetImageParams
specifier|static
class|class
name|GetImageParams
block|{
DECL|field|isGetImage
specifier|private
name|boolean
name|isGetImage
decl_stmt|;
DECL|field|isGetEdit
specifier|private
name|boolean
name|isGetEdit
decl_stmt|;
DECL|field|isPutImage
specifier|private
name|boolean
name|isPutImage
decl_stmt|;
DECL|field|remoteport
specifier|private
name|int
name|remoteport
decl_stmt|;
DECL|field|machineName
specifier|private
name|String
name|machineName
decl_stmt|;
DECL|field|startTxId
DECL|field|endTxId
DECL|field|txId
specifier|private
name|long
name|startTxId
decl_stmt|,
name|endTxId
decl_stmt|,
name|txId
decl_stmt|;
DECL|field|storageInfoString
specifier|private
name|String
name|storageInfoString
decl_stmt|;
DECL|field|fetchLatest
specifier|private
name|boolean
name|fetchLatest
decl_stmt|;
comment|/**      * @param request the object from which this servlet reads the url contents      * @param response the object into which this servlet writes the url contents      * @throws IOException if the request is bad      */
DECL|method|GetImageParams (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|GetImageParams
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|pmap
init|=
name|request
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
name|isGetImage
operator|=
name|isGetEdit
operator|=
name|isPutImage
operator|=
name|fetchLatest
operator|=
literal|false
expr_stmt|;
name|remoteport
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
range|:
name|pmap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
index|[]
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"getimage"
argument_list|)
condition|)
block|{
name|isGetImage
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|txId
operator|=
name|ServletUtil
operator|.
name|parseLongParam
argument_list|(
name|request
argument_list|,
name|TXID_PARAM
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|getParameter
argument_list|(
name|TXID_PARAM
argument_list|)
operator|.
name|equals
argument_list|(
name|LATEST_FSIMAGE_VALUE
argument_list|)
condition|)
block|{
name|fetchLatest
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|nfe
throw|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"getedit"
argument_list|)
condition|)
block|{
name|isGetEdit
operator|=
literal|true
expr_stmt|;
name|startTxId
operator|=
name|ServletUtil
operator|.
name|parseLongParam
argument_list|(
name|request
argument_list|,
name|START_TXID_PARAM
argument_list|)
expr_stmt|;
name|endTxId
operator|=
name|ServletUtil
operator|.
name|parseLongParam
argument_list|(
name|request
argument_list|,
name|END_TXID_PARAM
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"putimage"
argument_list|)
condition|)
block|{
name|isPutImage
operator|=
literal|true
expr_stmt|;
name|txId
operator|=
name|ServletUtil
operator|.
name|parseLongParam
argument_list|(
name|request
argument_list|,
name|TXID_PARAM
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"port"
argument_list|)
condition|)
block|{
name|remoteport
operator|=
operator|new
name|Integer
argument_list|(
name|val
index|[
literal|0
index|]
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"machine"
argument_list|)
condition|)
block|{
name|machineName
operator|=
name|val
index|[
literal|0
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|STORAGEINFO_PARAM
argument_list|)
condition|)
block|{
name|storageInfoString
operator|=
name|val
index|[
literal|0
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|machineName
operator|==
literal|null
condition|)
block|{
name|machineName
operator|=
name|request
operator|.
name|getRemoteHost
argument_list|()
expr_stmt|;
if|if
condition|(
name|InetAddresses
operator|.
name|isInetAddress
argument_list|(
name|machineName
argument_list|)
condition|)
block|{
name|machineName
operator|=
name|NetUtils
operator|.
name|getHostNameOfIP
argument_list|(
name|machineName
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|numGets
init|=
operator|(
name|isGetImage
condition|?
literal|1
else|:
literal|0
operator|)
operator|+
operator|(
name|isGetEdit
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
if|if
condition|(
operator|(
name|numGets
operator|>
literal|1
operator|)
operator|||
operator|(
name|numGets
operator|==
literal|0
operator|)
operator|&&
operator|!
name|isPutImage
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Illegal parameters to TransferFsImage"
argument_list|)
throw|;
block|}
block|}
DECL|method|getStorageInfoString ()
specifier|public
name|String
name|getStorageInfoString
parameter_list|()
block|{
return|return
name|storageInfoString
return|;
block|}
DECL|method|getTxId ()
specifier|public
name|long
name|getTxId
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isGetImage
operator|||
name|isPutImage
argument_list|)
expr_stmt|;
return|return
name|txId
return|;
block|}
DECL|method|getStartTxId ()
specifier|public
name|long
name|getStartTxId
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isGetEdit
argument_list|)
expr_stmt|;
return|return
name|startTxId
return|;
block|}
DECL|method|getEndTxId ()
specifier|public
name|long
name|getEndTxId
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isGetEdit
argument_list|)
expr_stmt|;
return|return
name|endTxId
return|;
block|}
DECL|method|isGetEdit ()
name|boolean
name|isGetEdit
parameter_list|()
block|{
return|return
name|isGetEdit
return|;
block|}
DECL|method|isGetImage ()
name|boolean
name|isGetImage
parameter_list|()
block|{
return|return
name|isGetImage
return|;
block|}
DECL|method|isPutImage ()
name|boolean
name|isPutImage
parameter_list|()
block|{
return|return
name|isPutImage
return|;
block|}
DECL|method|getInfoServer ()
name|String
name|getInfoServer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|machineName
operator|==
literal|null
operator|||
name|remoteport
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MachineName and port undefined"
argument_list|)
throw|;
block|}
return|return
name|machineName
operator|+
literal|":"
operator|+
name|remoteport
return|;
block|}
DECL|method|shouldFetchLatest ()
name|boolean
name|shouldFetchLatest
parameter_list|()
block|{
return|return
name|fetchLatest
return|;
block|}
block|}
block|}
end_class

end_unit

