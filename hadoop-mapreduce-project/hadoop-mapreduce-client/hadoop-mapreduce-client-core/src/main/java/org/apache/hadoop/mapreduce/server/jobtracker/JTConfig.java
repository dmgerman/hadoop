begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.server.jobtracker
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|server
operator|.
name|jobtracker
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
name|mapreduce
operator|.
name|MRConfig
import|;
end_import

begin_comment
comment|/**  * Place holder for JobTracker server-level configuration.  *   * The keys should have "mapreduce.jobtracker." as the prefix  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|JTConfig
specifier|public
interface|interface
name|JTConfig
extends|extends
name|MRConfig
block|{
comment|// JobTracker configuration parameters
DECL|field|JT_IPC_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|JT_IPC_ADDRESS
init|=
literal|"mapreduce.jobtracker.address"
decl_stmt|;
DECL|field|JT_PERSIST_JOBSTATUS
specifier|public
specifier|static
specifier|final
name|String
name|JT_PERSIST_JOBSTATUS
init|=
literal|"mapreduce.jobtracker.persist.jobstatus.active"
decl_stmt|;
DECL|field|JT_RETIREJOBS
specifier|public
specifier|static
specifier|final
name|String
name|JT_RETIREJOBS
init|=
literal|"mapreduce.jobtracker.retirejobs"
decl_stmt|;
DECL|field|JT_TASKCACHE_LEVELS
specifier|public
specifier|static
specifier|final
name|String
name|JT_TASKCACHE_LEVELS
init|=
literal|"mapreduce.jobtracker.taskcache.levels"
decl_stmt|;
DECL|field|JT_SYSTEM_DIR
specifier|public
specifier|static
specifier|final
name|String
name|JT_SYSTEM_DIR
init|=
literal|"mapreduce.jobtracker.system.dir"
decl_stmt|;
DECL|field|JT_STAGING_AREA_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|JT_STAGING_AREA_ROOT
init|=
literal|"mapreduce.jobtracker.staging.root.dir"
decl_stmt|;
DECL|field|JT_MAX_MAPMEMORY_MB
specifier|public
specifier|static
specifier|final
name|String
name|JT_MAX_MAPMEMORY_MB
init|=
literal|"mapreduce.jobtracker.maxmapmemory.mb"
decl_stmt|;
DECL|field|JT_MAX_REDUCEMEMORY_MB
specifier|public
specifier|static
specifier|final
name|String
name|JT_MAX_REDUCEMEMORY_MB
init|=
literal|"mapreduce.jobtracker.maxreducememory.mb"
decl_stmt|;
DECL|field|JT_USER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|JT_USER_NAME
init|=
literal|"mapreduce.jobtracker.kerberos.principal"
decl_stmt|;
block|}
end_interface

end_unit

