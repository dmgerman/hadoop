begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|classification
operator|.
name|InterfaceStability
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
name|PathFilter
import|;
end_import

begin_import
import|import static
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
name|LocalResourceType
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Constants used in both Client and Application Master.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DynoConstants
specifier|public
specifier|final
class|class
name|DynoConstants
block|{
DECL|method|DynoConstants ()
specifier|private
name|DynoConstants
parameter_list|()
block|{}
comment|// Directory to use for remote storage (a location on the remote FS which
comment|// can be accessed by all components). This will be the name of the directory
comment|// within the submitter's home directory.
DECL|field|DYNAMOMETER_STORAGE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DYNAMOMETER_STORAGE_DIR
init|=
literal|".dynamometer"
decl_stmt|;
comment|/* The following used for Client -> AM communication */
comment|// Resource for the zip file of all of the configuration for the
comment|// DataNodes/NameNode
DECL|field|CONF_ZIP
specifier|public
specifier|static
specifier|final
name|DynoResource
name|CONF_ZIP
init|=
operator|new
name|DynoResource
argument_list|(
literal|"CONF_ZIP"
argument_list|,
name|ARCHIVE
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
comment|// Resource for the Hadoop binary archive (distribution tar)
DECL|field|HADOOP_BINARY
specifier|public
specifier|static
specifier|final
name|DynoResource
name|HADOOP_BINARY
init|=
operator|new
name|DynoResource
argument_list|(
literal|"HADOOP_BINARY"
argument_list|,
name|ARCHIVE
argument_list|,
literal|"hadoopBinary"
argument_list|)
decl_stmt|;
comment|// Resource for the script used to start the DataNodes/NameNode
DECL|field|START_SCRIPT
specifier|public
specifier|static
specifier|final
name|DynoResource
name|START_SCRIPT
init|=
operator|new
name|DynoResource
argument_list|(
literal|"START_SCRIPT"
argument_list|,
name|FILE
argument_list|,
literal|"start-component.sh"
argument_list|)
decl_stmt|;
comment|// Resource for the file system image file used by the NameNode
DECL|field|FS_IMAGE
specifier|public
specifier|static
specifier|final
name|DynoResource
name|FS_IMAGE
init|=
operator|new
name|DynoResource
argument_list|(
literal|"FS_IMAGE"
argument_list|,
name|FILE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Resource for the md5 file accompanying the file system image for the
comment|// NameNode
DECL|field|FS_IMAGE_MD5
specifier|public
specifier|static
specifier|final
name|DynoResource
name|FS_IMAGE_MD5
init|=
operator|new
name|DynoResource
argument_list|(
literal|"FS_IMAGE_MD5"
argument_list|,
name|FILE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Resource for the VERSION file accompanying the file system image
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|DynoResource
name|VERSION
init|=
operator|new
name|DynoResource
argument_list|(
literal|"VERSION"
argument_list|,
name|FILE
argument_list|,
literal|"VERSION"
argument_list|)
decl_stmt|;
comment|// Resource for the archive containing all dependencies
DECL|field|DYNO_DEPENDENCIES
specifier|public
specifier|static
specifier|final
name|DynoResource
name|DYNO_DEPENDENCIES
init|=
operator|new
name|DynoResource
argument_list|(
literal|"DYNO_DEPS"
argument_list|,
name|ARCHIVE
argument_list|,
literal|"dependencies"
argument_list|)
decl_stmt|;
comment|// Environment variable which will contain the location of the directory
comment|// which holds all of the block files for the DataNodes
DECL|field|BLOCK_LIST_PATH_ENV
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_LIST_PATH_ENV
init|=
literal|"BLOCK_ZIP_PATH"
decl_stmt|;
comment|// The format of the name of a single block file
DECL|field|BLOCK_LIST_FILE_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|BLOCK_LIST_FILE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"dn[0-9]+-a-[0-9]+-r-[0-9]+"
argument_list|)
decl_stmt|;
comment|// The file name to use when localizing the block file on a DataNode; will be
comment|// suffixed with an integer
DECL|field|BLOCK_LIST_RESOURCE_PATH_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_LIST_RESOURCE_PATH_PREFIX
init|=
literal|"blocks/block"
decl_stmt|;
DECL|field|BLOCK_LIST_FILE_FILTER
specifier|public
specifier|static
specifier|final
name|PathFilter
name|BLOCK_LIST_FILE_FILTER
init|=
parameter_list|(
name|path
parameter_list|)
lambda|->
name|DynoConstants
operator|.
name|BLOCK_LIST_FILE_PATTERN
operator|.
name|matcher
argument_list|(
name|path
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
decl_stmt|;
comment|// Environment variable which will contain the full path of the directory
comment|// which should be used for remote (shared) storage
DECL|field|REMOTE_STORAGE_PATH_ENV
specifier|public
specifier|static
specifier|final
name|String
name|REMOTE_STORAGE_PATH_ENV
init|=
literal|"REMOTE_STORAGE_PATH"
decl_stmt|;
comment|// Environment variable which will contain the RPC address of the NameNode
comment|// which the DataNodes should contact, if the NameNode is not launched
comment|// internally by this application
DECL|field|REMOTE_NN_RPC_ADDR_ENV
specifier|public
specifier|static
specifier|final
name|String
name|REMOTE_NN_RPC_ADDR_ENV
init|=
literal|"REMOTE_NN_RPC_ADDR"
decl_stmt|;
comment|// Environment variable which will contain the view ACLs for the launched
comment|// containers.
DECL|field|JOB_ACL_VIEW_ENV
specifier|public
specifier|static
specifier|final
name|String
name|JOB_ACL_VIEW_ENV
init|=
literal|"JOB_ACL_VIEW"
decl_stmt|;
comment|/* The following used for AM -> DN, NN communication */
comment|// The name of the file which will store information about the NameNode
comment|// (within the remote storage directory)
DECL|field|NN_INFO_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|NN_INFO_FILE_NAME
init|=
literal|"nn_info.prop"
decl_stmt|;
comment|// Environment variable which will contain additional arguments for the
comment|// NameNode
DECL|field|NN_ADDITIONAL_ARGS_ENV
specifier|public
specifier|static
specifier|final
name|String
name|NN_ADDITIONAL_ARGS_ENV
init|=
literal|"NN_ADDITIONAL_ARGS"
decl_stmt|;
comment|// Environment variable which will contain additional arguments for the
comment|// DataNode
DECL|field|DN_ADDITIONAL_ARGS_ENV
specifier|public
specifier|static
specifier|final
name|String
name|DN_ADDITIONAL_ARGS_ENV
init|=
literal|"DN_ADDITIONAL_ARGS"
decl_stmt|;
comment|// Environment variable which will contain the directory to use for the
comment|// NameNode's name directory;
comment|// if not specified a directory within the YARN container working directory
comment|// will be used.
DECL|field|NN_NAME_DIR_ENV
specifier|public
specifier|static
specifier|final
name|String
name|NN_NAME_DIR_ENV
init|=
literal|"NN_NAME_DIR"
decl_stmt|;
comment|// Environment variable which will contain the directory to use for the
comment|// NameNode's edits directory;
comment|// if not specified a directory within the YARN container working directory
comment|// will be used.
DECL|field|NN_EDITS_DIR_ENV
specifier|public
specifier|static
specifier|final
name|String
name|NN_EDITS_DIR_ENV
init|=
literal|"NN_EDITS_DIR"
decl_stmt|;
DECL|field|NN_FILE_METRIC_PERIOD_ENV
specifier|public
specifier|static
specifier|final
name|String
name|NN_FILE_METRIC_PERIOD_ENV
init|=
literal|"NN_FILE_METRIC_PERIOD"
decl_stmt|;
comment|/*    * These are used as the names of properties and as the environment variables    */
comment|// The port to use on the NameNode host when contacting for client RPCs
DECL|field|NN_RPC_PORT
specifier|public
specifier|static
specifier|final
name|String
name|NN_RPC_PORT
init|=
literal|"NN_RPC_PORT"
decl_stmt|;
comment|// The hostname of the machine running the NameNode
DECL|field|NN_HOSTNAME
specifier|public
specifier|static
specifier|final
name|String
name|NN_HOSTNAME
init|=
literal|"NN_HOSTNAME"
decl_stmt|;
comment|// The port to use on the NameNode host when contacting for service RPCs
DECL|field|NN_SERVICERPC_PORT
specifier|public
specifier|static
specifier|final
name|String
name|NN_SERVICERPC_PORT
init|=
literal|"NN_SERVICERPC_PORT"
decl_stmt|;
comment|// The port to use on the NameNode host when contacting for HTTP access
DECL|field|NN_HTTP_PORT
specifier|public
specifier|static
specifier|final
name|String
name|NN_HTTP_PORT
init|=
literal|"NN_HTTP_PORT"
decl_stmt|;
block|}
end_class

end_unit

