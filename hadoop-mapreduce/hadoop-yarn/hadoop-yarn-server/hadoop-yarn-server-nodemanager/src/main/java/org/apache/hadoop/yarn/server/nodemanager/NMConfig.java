begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
package|;
end_package

begin_comment
comment|/** this class stores all the configuration constant keys   * for the nodemanager. All the configuration key variables  * that are going to be used in the nodemanager should be   * stored here. This allows us to see all the configuration   * parameters at one place.  */
end_comment

begin_class
DECL|class|NMConfig
specifier|public
class|class
name|NMConfig
block|{
DECL|field|NM_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|NM_PREFIX
init|=
literal|"yarn.server.nodemanager."
decl_stmt|;
DECL|field|DEFAULT_NM_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_BIND_ADDRESS
init|=
literal|"0.0.0.0:45454"
decl_stmt|;
comment|/** host:port address to which to bind to **/
DECL|field|NM_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|NM_BIND_ADDRESS
init|=
name|NM_PREFIX
operator|+
literal|"address"
decl_stmt|;
DECL|field|DEFAULT_NM_HTTP_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_HTTP_BIND_ADDRESS
init|=
literal|"0.0.0.0:9999"
decl_stmt|;
comment|/** host:port address to which webserver has to bind to **/
DECL|field|NM_HTTP_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|NM_HTTP_BIND_ADDRESS
init|=
name|NM_PREFIX
operator|+
literal|"http-address"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCALIZER_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_LOCALIZER_BIND_ADDRESS
init|=
literal|"0.0.0.0:4344"
decl_stmt|;
DECL|field|NM_LOCALIZER_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCALIZER_BIND_ADDRESS
init|=
name|NM_PREFIX
operator|+
literal|"localizer.address"
decl_stmt|;
DECL|field|NM_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|NM_KEYTAB
init|=
name|NM_PREFIX
operator|+
literal|"keytab"
decl_stmt|;
DECL|field|NM_CONTAINER_EXECUTOR_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|NM_CONTAINER_EXECUTOR_CLASS
init|=
name|NM_PREFIX
operator|+
literal|"container-executor.class"
decl_stmt|;
DECL|field|NM_LOCAL_DIR
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCAL_DIR
init|=
name|NM_PREFIX
operator|+
literal|"local-dir"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCAL_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_LOCAL_DIR
init|=
literal|"/tmp/nm-local-dir"
decl_stmt|;
DECL|field|NM_LOG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOG_DIR
init|=
name|NM_PREFIX
operator|+
literal|"log.dir"
decl_stmt|;
comment|// TODO: Rename
DECL|field|DEFAULT_NM_LOG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_LOG_DIR
init|=
literal|"/tmp/logs"
decl_stmt|;
DECL|field|REMOTE_USER_LOG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|REMOTE_USER_LOG_DIR
init|=
name|NM_PREFIX
operator|+
literal|"remote-app-log-dir"
decl_stmt|;
DECL|field|DEFAULT_REMOTE_APP_LOG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_REMOTE_APP_LOG_DIR
init|=
literal|"/tmp/logs"
decl_stmt|;
DECL|field|DEFAULT_NM_VMEM_GB
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_VMEM_GB
init|=
literal|8
decl_stmt|;
DECL|field|NM_VMEM_GB
specifier|public
specifier|static
specifier|final
name|String
name|NM_VMEM_GB
init|=
name|NM_PREFIX
operator|+
literal|"resource.memory.gb"
decl_stmt|;
comment|// TODO: Should this instead be dictated by RM?
DECL|field|HEARTBEAT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HEARTBEAT_INTERVAL
init|=
name|NM_PREFIX
operator|+
literal|"heartbeat-interval"
decl_stmt|;
DECL|field|DEFAULT_HEARTBEAT_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_HEARTBEAT_INTERVAL
init|=
literal|1000
decl_stmt|;
DECL|field|NM_MAX_DELETE_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|NM_MAX_DELETE_THREADS
init|=
name|NM_PREFIX
operator|+
literal|"max.delete.threads"
decl_stmt|;
DECL|field|DEFAULT_MAX_DELETE_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_DELETE_THREADS
init|=
literal|4
decl_stmt|;
DECL|field|NM_MAX_PUBLIC_FETCH_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|NM_MAX_PUBLIC_FETCH_THREADS
init|=
name|NM_PREFIX
operator|+
literal|"max.public.fetch.threads"
decl_stmt|;
DECL|field|DEFAULT_MAX_PUBLIC_FETCH_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_PUBLIC_FETCH_THREADS
init|=
literal|4
decl_stmt|;
DECL|field|NM_LOCALIZATION_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCALIZATION_THREADS
init|=
name|NM_PREFIX
operator|+
literal|"localiation.threads"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCALIZATION_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_LOCALIZATION_THREADS
init|=
literal|5
decl_stmt|;
DECL|field|NM_CONTAINER_MGR_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|NM_CONTAINER_MGR_THREADS
init|=
name|NM_PREFIX
operator|+
literal|"container.manager.threads"
decl_stmt|;
DECL|field|DEFAULT_NM_CONTAINER_MGR_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_CONTAINER_MGR_THREADS
init|=
literal|5
decl_stmt|;
DECL|field|NM_TARGET_CACHE_MB
specifier|public
specifier|static
specifier|final
name|String
name|NM_TARGET_CACHE_MB
init|=
name|NM_PREFIX
operator|+
literal|"target.cache.size"
decl_stmt|;
DECL|field|DEFAULT_NM_TARGET_CACHE_MB
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_NM_TARGET_CACHE_MB
init|=
literal|10
operator|*
literal|1024
decl_stmt|;
DECL|field|NM_CACHE_CLEANUP_MS
specifier|public
specifier|static
specifier|final
name|String
name|NM_CACHE_CLEANUP_MS
init|=
name|NM_PREFIX
operator|+
literal|"target.cache.cleanup.period.ms"
decl_stmt|;
DECL|field|DEFAULT_NM_CACHE_CLEANUP_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_NM_CACHE_CLEANUP_MS
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
block|}
end_class

end_unit

