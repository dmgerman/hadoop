begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
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
name|ozone
operator|.
name|OzoneAcl
import|;
end_import

begin_comment
comment|/**  * KSM Constants.  */
end_comment

begin_class
DECL|class|KSMConfigKeys
specifier|public
specifier|final
class|class
name|KSMConfigKeys
block|{
comment|/**    * Never constructed.    */
DECL|method|KSMConfigKeys ()
specifier|private
name|KSMConfigKeys
parameter_list|()
block|{   }
DECL|field|OZONE_KSM_HANDLER_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_HANDLER_COUNT_KEY
init|=
literal|"ozone.ksm.handler.count.key"
decl_stmt|;
DECL|field|OZONE_KSM_HANDLER_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KSM_HANDLER_COUNT_DEFAULT
init|=
literal|200
decl_stmt|;
DECL|field|OZONE_KSM_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_ADDRESS_KEY
init|=
literal|"ozone.ksm.address"
decl_stmt|;
DECL|field|OZONE_KSM_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_KSM_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KSM_PORT_DEFAULT
init|=
literal|9862
decl_stmt|;
DECL|field|OZONE_KSM_HTTP_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_HTTP_ENABLED_KEY
init|=
literal|"ozone.ksm.http.enabled"
decl_stmt|;
DECL|field|OZONE_KSM_HTTP_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_HTTP_BIND_HOST_KEY
init|=
literal|"ozone.ksm.http-bind-host"
decl_stmt|;
DECL|field|OZONE_KSM_HTTPS_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_HTTPS_BIND_HOST_KEY
init|=
literal|"ozone.ksm.https-bind-host"
decl_stmt|;
DECL|field|OZONE_KSM_HTTP_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_HTTP_ADDRESS_KEY
init|=
literal|"ozone.ksm.http-address"
decl_stmt|;
DECL|field|OZONE_KSM_HTTPS_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_HTTPS_ADDRESS_KEY
init|=
literal|"ozone.ksm.https-address"
decl_stmt|;
DECL|field|OZONE_KSM_KEYTAB_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_KEYTAB_FILE
init|=
literal|"ozone.ksm.keytab.file"
decl_stmt|;
DECL|field|OZONE_KSM_HTTP_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_HTTP_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_KSM_HTTP_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KSM_HTTP_BIND_PORT_DEFAULT
init|=
literal|9874
decl_stmt|;
DECL|field|OZONE_KSM_HTTPS_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KSM_HTTPS_BIND_PORT_DEFAULT
init|=
literal|9875
decl_stmt|;
comment|// LevelDB cache file uses an off-heap cache in LevelDB of 128 MB.
DECL|field|OZONE_KSM_DB_CACHE_SIZE_MB
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_DB_CACHE_SIZE_MB
init|=
literal|"ozone.ksm.leveldb.cache.size.mb"
decl_stmt|;
DECL|field|OZONE_KSM_DB_CACHE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KSM_DB_CACHE_SIZE_DEFAULT
init|=
literal|128
decl_stmt|;
DECL|field|OZONE_KSM_USER_MAX_VOLUME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_USER_MAX_VOLUME
init|=
literal|"ozone.ksm.user.max.volume"
decl_stmt|;
DECL|field|OZONE_KSM_USER_MAX_VOLUME_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KSM_USER_MAX_VOLUME_DEFAULT
init|=
literal|1024
decl_stmt|;
comment|// KSM Default user/group permissions
DECL|field|OZONE_KSM_USER_RIGHTS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_USER_RIGHTS
init|=
literal|"ozone.ksm.user.rights"
decl_stmt|;
DECL|field|OZONE_KSM_USER_RIGHTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|OzoneAcl
operator|.
name|OzoneACLRights
name|OZONE_KSM_USER_RIGHTS_DEFAULT
init|=
name|OzoneAcl
operator|.
name|OzoneACLRights
operator|.
name|READ_WRITE
decl_stmt|;
DECL|field|OZONE_KSM_GROUP_RIGHTS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_GROUP_RIGHTS
init|=
literal|"ozone.ksm.group.rights"
decl_stmt|;
DECL|field|OZONE_KSM_GROUP_RIGHTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|OzoneAcl
operator|.
name|OzoneACLRights
name|OZONE_KSM_GROUP_RIGHTS_DEFAULT
init|=
name|OzoneAcl
operator|.
name|OzoneACLRights
operator|.
name|READ_WRITE
decl_stmt|;
block|}
end_class

end_unit

