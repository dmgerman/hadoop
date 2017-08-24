begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
package|;
end_package

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
operator|.
name|Private
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
name|fs
operator|.
name|FileContext
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|RemoteIterator
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
annotation|@
name|Private
DECL|class|LogAggregationUtils
specifier|public
class|class
name|LogAggregationUtils
block|{
DECL|field|TMP_FILE_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|TMP_FILE_SUFFIX
init|=
literal|".tmp"
decl_stmt|;
comment|/**    * Constructs the full filename for an application's log file per node.    * @param remoteRootLogDir    * @param appId    * @param user    * @param nodeId    * @param suffix    * @return the remote log file.    */
DECL|method|getRemoteNodeLogFileForApp (Path remoteRootLogDir, ApplicationId appId, String user, NodeId nodeId, String suffix)
specifier|public
specifier|static
name|Path
name|getRemoteNodeLogFileForApp
parameter_list|(
name|Path
name|remoteRootLogDir
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getRemoteAppLogDir
argument_list|(
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|,
name|user
argument_list|,
name|suffix
argument_list|)
argument_list|,
name|getNodeString
argument_list|(
name|nodeId
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Gets the remote app log dir.    * @param remoteRootLogDir    * @param appId    * @param user    * @param suffix    * @return the remote application specific log dir.    */
DECL|method|getRemoteAppLogDir (Path remoteRootLogDir, ApplicationId appId, String user, String suffix)
specifier|public
specifier|static
name|Path
name|getRemoteAppLogDir
parameter_list|(
name|Path
name|remoteRootLogDir
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getRemoteLogSuffixedDir
argument_list|(
name|remoteRootLogDir
argument_list|,
name|user
argument_list|,
name|suffix
argument_list|)
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Gets the remote suffixed log dir for the user.    * @param remoteRootLogDir    * @param user    * @param suffix    * @return the remote suffixed log dir.    */
DECL|method|getRemoteLogSuffixedDir (Path remoteRootLogDir, String user, String suffix)
specifier|public
specifier|static
name|Path
name|getRemoteLogSuffixedDir
parameter_list|(
name|Path
name|remoteRootLogDir
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|suffix
operator|==
literal|null
operator|||
name|suffix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|getRemoteLogUserDir
argument_list|(
name|remoteRootLogDir
argument_list|,
name|user
argument_list|)
return|;
block|}
comment|// TODO Maybe support suffix to be more than a single file.
return|return
operator|new
name|Path
argument_list|(
name|getRemoteLogUserDir
argument_list|(
name|remoteRootLogDir
argument_list|,
name|user
argument_list|)
argument_list|,
name|suffix
argument_list|)
return|;
block|}
comment|/**    * Gets the remote log user dir.    * @param remoteRootLogDir    * @param user    * @return the remote per user log dir.    */
DECL|method|getRemoteLogUserDir (Path remoteRootLogDir, String user)
specifier|public
specifier|static
name|Path
name|getRemoteLogUserDir
parameter_list|(
name|Path
name|remoteRootLogDir
parameter_list|,
name|String
name|user
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|remoteRootLogDir
argument_list|,
name|user
argument_list|)
return|;
block|}
comment|/**    * Returns the suffix component of the log dir.    * @param conf    * @return the suffix which will be appended to the user log dir.    */
DECL|method|getRemoteNodeLogDirSuffix (Configuration conf)
specifier|public
specifier|static
name|String
name|getRemoteNodeLogDirSuffix
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR_SUFFIX
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_REMOTE_APP_LOG_DIR_SUFFIX
argument_list|)
return|;
block|}
comment|/**    * Converts a nodeId to a form used in the app log file name.    * @param nodeId    * @return the node string to be used to construct the file name.    */
annotation|@
name|VisibleForTesting
DECL|method|getNodeString (NodeId nodeId)
specifier|public
specifier|static
name|String
name|getNodeString
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
return|return
name|nodeId
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNodeString (String nodeId)
specifier|public
specifier|static
name|String
name|getNodeString
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
return|return
name|nodeId
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
return|;
block|}
comment|/**    * Return the remote application log directory.    * @param conf the configuration    * @param appId the application    * @param appOwner the application owner    * @return the remote application log directory path    * @throws IOException if we can not find remote application log directory    */
DECL|method|getRemoteAppLogDir ( Configuration conf, ApplicationId appId, String appOwner)
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
name|getRemoteAppLogDir
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|appOwner
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|suffix
init|=
name|LogAggregationUtils
operator|.
name|getRemoteNodeLogDirSuffix
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
name|remoteRootLogDir
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_REMOTE_APP_LOG_DIR
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|getRemoteAppLogDir
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|appOwner
argument_list|,
name|remoteRootLogDir
argument_list|,
name|suffix
argument_list|)
return|;
block|}
comment|/**    * Return the remote application log directory.    * @param conf the configuration    * @param appId the application    * @param appOwner the application owner    * @param remoteRootLogDir the remote root log directory    * @param suffix the log directory suffix    * @return the remote application log directory path    * @throws IOException if we can not find remote application log directory    */
DECL|method|getRemoteAppLogDir ( Configuration conf, ApplicationId appId, String appOwner, org.apache.hadoop.fs.Path remoteRootLogDir, String suffix)
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
name|getRemoteAppLogDir
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|appOwner
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
name|remoteRootLogDir
parameter_list|,
name|String
name|suffix
parameter_list|)
throws|throws
name|IOException
block|{
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
name|remoteAppDir
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|appOwner
operator|==
literal|null
condition|)
block|{
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
name|qualifiedRemoteRootLogDir
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|conf
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|remoteRootLogDir
argument_list|)
decl_stmt|;
name|FileContext
name|fc
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|qualifiedRemoteRootLogDir
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
name|toMatch
init|=
name|LogAggregationUtils
operator|.
name|getRemoteAppLogDir
argument_list|(
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|,
literal|"*"
argument_list|,
name|suffix
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|matching
init|=
name|fc
operator|.
name|util
argument_list|()
operator|.
name|globStatus
argument_list|(
name|toMatch
argument_list|)
decl_stmt|;
if|if
condition|(
name|matching
operator|==
literal|null
operator|||
name|matching
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can not find remote application directory for "
operator|+
literal|"the application:"
operator|+
name|appId
argument_list|)
throw|;
block|}
name|remoteAppDir
operator|=
name|matching
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|remoteAppDir
operator|=
name|LogAggregationUtils
operator|.
name|getRemoteAppLogDir
argument_list|(
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|,
name|appOwner
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
block|}
return|return
name|remoteAppDir
return|;
block|}
comment|/**    * Get all available log files under remote app log directory.    * @param conf the configuration    * @param appId the applicationId    * @param appOwner the application owner    * @param remoteRootLogDir the remote root log directory    * @param suffix the log directory suffix    * @return the iterator of available log files    * @throws IOException if there is no log file available    */
DECL|method|getRemoteNodeFileDir ( Configuration conf, ApplicationId appId, String appOwner, org.apache.hadoop.fs.Path remoteRootLogDir, String suffix)
specifier|public
specifier|static
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|getRemoteNodeFileDir
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|appOwner
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
name|remoteRootLogDir
parameter_list|,
name|String
name|suffix
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|remoteAppLogDir
init|=
name|getRemoteAppLogDir
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|appOwner
argument_list|,
name|remoteRootLogDir
argument_list|,
name|suffix
argument_list|)
decl_stmt|;
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|nodeFiles
init|=
literal|null
decl_stmt|;
name|Path
name|qualifiedLogDir
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|conf
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|remoteAppLogDir
argument_list|)
decl_stmt|;
name|nodeFiles
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|qualifiedLogDir
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
operator|.
name|listStatus
argument_list|(
name|remoteAppLogDir
argument_list|)
expr_stmt|;
return|return
name|nodeFiles
return|;
block|}
comment|/**    * Get all available log files under remote app log directory.    * @param conf the configuration    * @param appId the applicationId    * @param appOwner the application owner    * @return the iterator of available log files    * @throws IOException if there is no log file available    */
DECL|method|getRemoteNodeFileDir ( Configuration conf, ApplicationId appId, String appOwner)
specifier|public
specifier|static
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|getRemoteNodeFileDir
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|appOwner
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|remoteAppLogDir
init|=
name|getRemoteAppLogDir
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|appOwner
argument_list|)
decl_stmt|;
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|nodeFiles
init|=
literal|null
decl_stmt|;
name|Path
name|qualifiedLogDir
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|conf
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|remoteAppLogDir
argument_list|)
decl_stmt|;
name|nodeFiles
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|qualifiedLogDir
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
operator|.
name|listStatus
argument_list|(
name|remoteAppLogDir
argument_list|)
expr_stmt|;
return|return
name|nodeFiles
return|;
block|}
block|}
end_class

end_unit

