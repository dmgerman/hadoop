begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
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

begin_comment
comment|/**  * Stores Job History configuration keys that can be set by administrators of  * the Job History server.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|JHAdminConfig
specifier|public
class|class
name|JHAdminConfig
block|{
comment|/** The prefix to all Job History configuration properties.*/
DECL|field|MR_HISTORY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_PREFIX
init|=
literal|"mapreduce.jobhistory."
decl_stmt|;
comment|/** host:port address for History Server API.*/
DECL|field|MR_HISTORY_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_ADDRESS
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"address"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_HISTORY_PORT
init|=
literal|10020
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MR_HISTORY_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_MR_HISTORY_PORT
decl_stmt|;
comment|/** If history cleaning should be enabled or not.*/
DECL|field|MR_HISTORY_CLEANER_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_CLEANER_ENABLE
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"cleaner.enable"
decl_stmt|;
comment|/** Run the History Cleaner every X ms.*/
DECL|field|MR_HISTORY_CLEANER_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_CLEANER_INTERVAL_MS
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"cleaner.interval-ms"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_CLEANER_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MR_HISTORY_CLEANER_INTERVAL_MS
init|=
literal|1
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000l
decl_stmt|;
comment|//1 day
comment|/** The number of threads to handle client API requests.*/
DECL|field|MR_HISTORY_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_CLIENT_THREAD_COUNT
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"client.thread-count"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_HISTORY_CLIENT_THREAD_COUNT
init|=
literal|10
decl_stmt|;
comment|/**    * Size of the date string cache. Effects the number of directories    * which will be scanned to find a job.    */
DECL|field|MR_HISTORY_DATESTRING_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_DATESTRING_CACHE_SIZE
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"datestring.cache.size"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_DATESTRING_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_HISTORY_DATESTRING_CACHE_SIZE
init|=
literal|200000
decl_stmt|;
comment|/** Path where history files should be stored for DONE jobs. **/
DECL|field|MR_HISTORY_DONE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_DONE_DIR
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"done-dir"
decl_stmt|;
comment|/**    *  Path where history files should be stored after a job finished and before    *  they are pulled into the job history server.    **/
DECL|field|MR_HISTORY_INTERMEDIATE_DONE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_INTERMEDIATE_DONE_DIR
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"intermediate-done-dir"
decl_stmt|;
comment|/** Size of the job list cache.*/
DECL|field|MR_HISTORY_JOBLIST_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_JOBLIST_CACHE_SIZE
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"joblist.cache.size"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_JOBLIST_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_HISTORY_JOBLIST_CACHE_SIZE
init|=
literal|20000
decl_stmt|;
comment|/** The location of the Kerberos keytab file.*/
DECL|field|MR_HISTORY_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_KEYTAB
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"keytab"
decl_stmt|;
comment|/** Size of the loaded job cache.*/
DECL|field|MR_HISTORY_LOADED_JOB_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_LOADED_JOB_CACHE_SIZE
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"loadedjobs.cache.size"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_LOADED_JOB_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_HISTORY_LOADED_JOB_CACHE_SIZE
init|=
literal|5
decl_stmt|;
comment|/**    * The maximum age of a job history file before it is deleted from the history    * server.    */
DECL|field|MR_HISTORY_MAX_AGE_MS
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_MAX_AGE_MS
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"max-age-ms"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_MAX_AGE
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MR_HISTORY_MAX_AGE
init|=
literal|7
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000L
decl_stmt|;
comment|//1 week
comment|/**    * Scan for history files to more from intermediate done dir to done dir    * every X ms.    */
DECL|field|MR_HISTORY_MOVE_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_MOVE_INTERVAL_MS
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"move.interval-ms"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_MOVE_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MR_HISTORY_MOVE_INTERVAL_MS
init|=
literal|3
operator|*
literal|60
operator|*
literal|1000l
decl_stmt|;
comment|//3 minutes
comment|/** The number of threads used to move files.*/
DECL|field|MR_HISTORY_MOVE_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_MOVE_THREAD_COUNT
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"move.thread-count"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_MOVE_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_HISTORY_MOVE_THREAD_COUNT
init|=
literal|3
decl_stmt|;
comment|/** The Kerberos principal for the history server.*/
DECL|field|MR_HISTORY_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_PRINCIPAL
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"principal"
decl_stmt|;
comment|/**The address the history server webapp is on.*/
DECL|field|MR_HISTORY_WEBAPP_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_WEBAPP_ADDRESS
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"webapp.address"
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_WEBAPP_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_HISTORY_WEBAPP_PORT
init|=
literal|19888
decl_stmt|;
DECL|field|DEFAULT_MR_HISTORY_WEBAPP_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MR_HISTORY_WEBAPP_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_MR_HISTORY_WEBAPP_PORT
decl_stmt|;
comment|/*    * HS Service Authorization    */
DECL|field|MR_HS_SECURITY_SERVICE_AUTHORIZATION
specifier|public
specifier|static
specifier|final
name|String
name|MR_HS_SECURITY_SERVICE_AUTHORIZATION
init|=
literal|"security.mrhs.client.protocol.acl"
decl_stmt|;
comment|/**    * The HistoryStorage class to use to cache history data.    */
DECL|field|MR_HISTORY_STORAGE
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_STORAGE
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"store.class"
decl_stmt|;
comment|/** Whether to use fixed ports with the minicluster. */
DECL|field|MR_HISTORY_MINICLUSTER_FIXED_PORTS
specifier|public
specifier|static
specifier|final
name|String
name|MR_HISTORY_MINICLUSTER_FIXED_PORTS
init|=
name|MR_HISTORY_PREFIX
operator|+
literal|"minicluster.fixed.ports"
decl_stmt|;
comment|/**    * Default is false to be able to run tests concurrently without port    * conflicts.    */
DECL|field|DEFAULT_MR_HISTORY_MINICLUSTER_FIXED_PORTS
specifier|public
specifier|static
name|boolean
name|DEFAULT_MR_HISTORY_MINICLUSTER_FIXED_PORTS
init|=
literal|false
decl_stmt|;
block|}
end_class

end_unit

