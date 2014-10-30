begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

begin_comment
comment|/**  * Some constants for IdMapping  */
end_comment

begin_class
DECL|class|IdMappingConstant
specifier|public
class|class
name|IdMappingConstant
block|{
comment|/** Do user/group update every 15 minutes by default, minimum 1 minute */
DECL|field|USERGROUPID_UPDATE_MILLIS_KEY
specifier|public
specifier|final
specifier|static
name|String
name|USERGROUPID_UPDATE_MILLIS_KEY
init|=
literal|"usergroupid.update.millis"
decl_stmt|;
DECL|field|USERGROUPID_UPDATE_MILLIS_DEFAULT
specifier|public
specifier|final
specifier|static
name|long
name|USERGROUPID_UPDATE_MILLIS_DEFAULT
init|=
literal|15
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// ms
DECL|field|USERGROUPID_UPDATE_MILLIS_MIN
specifier|public
specifier|final
specifier|static
name|long
name|USERGROUPID_UPDATE_MILLIS_MIN
init|=
literal|1
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// ms
DECL|field|UNKNOWN_USER
specifier|public
specifier|final
specifier|static
name|String
name|UNKNOWN_USER
init|=
literal|"nobody"
decl_stmt|;
DECL|field|UNKNOWN_GROUP
specifier|public
specifier|final
specifier|static
name|String
name|UNKNOWN_GROUP
init|=
literal|"nobody"
decl_stmt|;
comment|// Used for finding the configured static mapping file.
DECL|field|STATIC_ID_MAPPING_FILE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|STATIC_ID_MAPPING_FILE_KEY
init|=
literal|"static.id.mapping.file"
decl_stmt|;
DECL|field|STATIC_ID_MAPPING_FILE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|STATIC_ID_MAPPING_FILE_DEFAULT
init|=
literal|"/etc/nfs.map"
decl_stmt|;
block|}
end_class

end_unit

