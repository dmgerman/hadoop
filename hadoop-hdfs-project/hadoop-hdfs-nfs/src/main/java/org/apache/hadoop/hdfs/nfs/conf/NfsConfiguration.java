begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|conf
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
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
name|IdMappingConstant
import|;
end_import

begin_comment
comment|/**  * Adds deprecated keys into the configuration.  */
end_comment

begin_class
DECL|class|NfsConfiguration
specifier|public
class|class
name|NfsConfiguration
extends|extends
name|HdfsConfiguration
block|{
static|static
block|{
name|addDeprecatedKeys
argument_list|()
expr_stmt|;
block|}
DECL|method|addDeprecatedKeys ()
specifier|private
specifier|static
name|void
name|addDeprecatedKeys
parameter_list|()
block|{
name|Configuration
operator|.
name|addDeprecations
argument_list|(
operator|new
name|DeprecationDelta
index|[]
block|{
operator|new
name|DeprecationDelta
argument_list|(
literal|"nfs3.server.port"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_SERVER_PORT_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"nfs3.mountd.port"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_MOUNTD_PORT_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs.exports.cache.size"
argument_list|,
name|Nfs3Constant
operator|.
name|NFS_EXPORTS_CACHE_SIZE_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs.exports.cache.expirytime.millis"
argument_list|,
name|Nfs3Constant
operator|.
name|NFS_EXPORTS_CACHE_EXPIRYTIME_MILLIS_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"hadoop.nfs.userupdate.milly"
argument_list|,
name|IdMappingConstant
operator|.
name|USERGROUPID_UPDATE_MILLIS_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"nfs.usergroup.update.millis"
argument_list|,
name|IdMappingConstant
operator|.
name|USERGROUPID_UPDATE_MILLIS_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"nfs.static.mapping.file"
argument_list|,
name|IdMappingConstant
operator|.
name|STATIC_ID_MAPPING_FILE_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs3.enableDump"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_FILE_DUMP_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs3.dump.dir"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_FILE_DUMP_DIR_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs3.max.open.files"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_MAX_OPEN_FILES_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs3.stream.timeout"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_STREAM_TIMEOUT_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs3.export.point"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_EXPORT_POINT_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"nfs.allow.insecure.ports"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_PORT_MONITORING_DISABLED_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs.keytab.file"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_KEYTAB_FILE_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs.kerberos.principal"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_KERBEROS_PRINCIPAL_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs.rtmax"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_MAX_READ_TRANSFER_SIZE_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs.wtmax"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_MAX_WRITE_TRANSFER_SIZE_KEY
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"dfs.nfs.dtmax"
argument_list|,
name|NfsConfigKeys
operator|.
name|DFS_NFS_MAX_READDIR_TRANSFER_SIZE_KEY
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

