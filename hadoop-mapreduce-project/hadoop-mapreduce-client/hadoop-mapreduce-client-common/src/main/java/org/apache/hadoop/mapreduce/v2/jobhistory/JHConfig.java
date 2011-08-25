begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_class
DECL|class|JHConfig
specifier|public
class|class
name|JHConfig
block|{
DECL|field|HS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|HS_PREFIX
init|=
literal|"yarn.server.historyserver."
decl_stmt|;
comment|/** host:port address to which to bind to **/
DECL|field|HS_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|HS_BIND_ADDRESS
init|=
name|HS_PREFIX
operator|+
literal|"address"
decl_stmt|;
DECL|field|HS_USER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HS_USER_NAME
init|=
name|HS_PREFIX
operator|+
literal|"kerberos.principal"
decl_stmt|;
DECL|field|HS_KEYTAB_FILE
specifier|public
specifier|static
specifier|final
name|String
name|HS_KEYTAB_FILE
init|=
name|HS_PREFIX
operator|+
literal|"jeytab.file"
decl_stmt|;
DECL|field|DEFAULT_HS_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_HS_BIND_ADDRESS
init|=
literal|"0.0.0.0:10020"
decl_stmt|;
comment|/** Done Dir for for AppMaster **/
DECL|field|HISTORY_INTERMEDIATE_DONE_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_INTERMEDIATE_DONE_DIR_KEY
init|=
literal|"yarn.historyfile.intermediateDoneDir"
decl_stmt|;
comment|/** Done Dir for for AppMaster **/
DECL|field|HISTORY_DONE_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_DONE_DIR_KEY
init|=
literal|"yarn.historyfile.doneDir"
decl_stmt|;
comment|/**    * Boolean. Create the base dirs in the JobHistoryEventHandler    * Set to false for multi-user clusters.    */
DECL|field|CREATE_HISTORY_INTERMEDIATE_BASE_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_HISTORY_INTERMEDIATE_BASE_DIR_KEY
init|=
literal|"yarn.history.create.intermediate.base.dir"
decl_stmt|;
comment|/** Done Dir for history server. **/
DECL|field|HISTORY_SERVER_DONE_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_SERVER_DONE_DIR_KEY
init|=
name|HS_PREFIX
operator|+
literal|"historyfile.doneDir"
decl_stmt|;
comment|/**    * Size of the job list cache.    */
DECL|field|HISTORY_SERVER_JOBLIST_CACHE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_SERVER_JOBLIST_CACHE_SIZE_KEY
init|=
name|HS_PREFIX
operator|+
literal|"joblist.cache.size"
decl_stmt|;
comment|/**    * Size of the loaded job cache.    */
DECL|field|HISTORY_SERVER_LOADED_JOB_CACHE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_SERVER_LOADED_JOB_CACHE_SIZE_KEY
init|=
name|HS_PREFIX
operator|+
literal|"loadedjobs.cache.size"
decl_stmt|;
comment|/**    * Size of the date string cache. Effects the number of directories    * which will be scanned to find a job.    */
DECL|field|HISTORY_SERVER_DATESTRING_CACHE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_SERVER_DATESTRING_CACHE_SIZE_KEY
init|=
name|HS_PREFIX
operator|+
literal|"datestring.cache.size"
decl_stmt|;
comment|/**    * The time interval in milliseconds for the history server    * to wake up and scan for files to be moved.    */
DECL|field|HISTORY_SERVER_MOVE_THREAD_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_SERVER_MOVE_THREAD_INTERVAL
init|=
name|HS_PREFIX
operator|+
literal|"move.thread.interval"
decl_stmt|;
comment|/**    * The number of threads used to move files.    */
DECL|field|HISTORY_SERVER_NUM_MOVE_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_SERVER_NUM_MOVE_THREADS
init|=
name|HS_PREFIX
operator|+
literal|"move.threads.count"
decl_stmt|;
comment|// Equivalent to 0.20 mapreduce.jobhistory.debug.mode
DECL|field|HISTORY_DEBUG_MODE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_DEBUG_MODE_KEY
init|=
name|HS_PREFIX
operator|+
literal|"debug.mode"
decl_stmt|;
DECL|field|HISTORY_MAXAGE
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_MAXAGE
init|=
literal|"yarn.historyfile.maxage"
decl_stmt|;
comment|//TODO Move some of the HistoryServer specific out into a separate configuration class.
DECL|field|HS_KEYTAB_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HS_KEYTAB_KEY
init|=
name|HS_PREFIX
operator|+
literal|"keytab"
decl_stmt|;
DECL|field|HS_SERVER_PRINCIPAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HS_SERVER_PRINCIPAL_KEY
init|=
literal|"yarn.historyserver.principal"
decl_stmt|;
DECL|field|RUN_HISTORY_CLEANER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|RUN_HISTORY_CLEANER_KEY
init|=
name|HS_PREFIX
operator|+
literal|"cleaner.run"
decl_stmt|;
comment|/**    * Run interval for the History Cleaner thread.    */
DECL|field|HISTORY_CLEANER_RUN_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_CLEANER_RUN_INTERVAL
init|=
name|HS_PREFIX
operator|+
literal|"cleaner.run.interval"
decl_stmt|;
DECL|field|HS_WEBAPP_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|HS_WEBAPP_BIND_ADDRESS
init|=
name|HS_PREFIX
operator|+
literal|"address.webapp"
decl_stmt|;
DECL|field|DEFAULT_HS_WEBAPP_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_HS_WEBAPP_BIND_ADDRESS
init|=
literal|"0.0.0.0:19888"
decl_stmt|;
DECL|field|HS_CLIENT_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|HS_CLIENT_THREADS
init|=
name|HS_PREFIX
operator|+
literal|"client.threads"
decl_stmt|;
DECL|field|DEFAULT_HS_CLIENT_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_HS_CLIENT_THREADS
init|=
literal|10
decl_stmt|;
comment|//From JTConfig. May need to be moved elsewhere.
DECL|field|JOBHISTORY_TASKPROGRESS_NUMBER_SPLITS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|JOBHISTORY_TASKPROGRESS_NUMBER_SPLITS_KEY
init|=
literal|"mapreduce.jobtracker.jobhistory.task.numberprogresssplits"
decl_stmt|;
DECL|field|DEFAULT_NUMBER_PROGRESS_SPLITS
specifier|public
specifier|static
name|int
name|DEFAULT_NUMBER_PROGRESS_SPLITS
init|=
literal|12
decl_stmt|;
block|}
end_class

end_unit

