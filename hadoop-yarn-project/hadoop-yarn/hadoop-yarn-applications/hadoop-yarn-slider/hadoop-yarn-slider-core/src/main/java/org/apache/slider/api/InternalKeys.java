begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
package|;
end_package

begin_comment
comment|/**  * Keys for internal use, go into `internal.json` and not intended for normal  * use except when tuning Slider AM operations  */
end_comment

begin_interface
DECL|interface|InternalKeys
specifier|public
interface|interface
name|InternalKeys
block|{
comment|/**    * Home dir of the app: {@value}    * If set, implies there is a home dir to use    */
DECL|field|INTERNAL_APPLICATION_HOME
name|String
name|INTERNAL_APPLICATION_HOME
init|=
literal|"internal.application.home"
decl_stmt|;
comment|/**    * Path to an image file containing the app: {@value}    */
DECL|field|INTERNAL_APPLICATION_IMAGE_PATH
name|String
name|INTERNAL_APPLICATION_IMAGE_PATH
init|=
literal|"internal.application.image.path"
decl_stmt|;
comment|/**    * Time in milliseconds to wait after forking any in-AM     * process before attempting to start up the containers: {@value}    *     * A shorter value brings the cluster up faster, but means that if the    * in AM process fails (due to a bad configuration), then time    * is wasted starting containers on a cluster that isn't going to come    * up    */
DECL|field|INTERNAL_CONTAINER_STARTUP_DELAY
name|String
name|INTERNAL_CONTAINER_STARTUP_DELAY
init|=
literal|"internal.container.startup.delay"
decl_stmt|;
comment|/**    * internal temp directory: {@value}    */
DECL|field|INTERNAL_AM_TMP_DIR
name|String
name|INTERNAL_AM_TMP_DIR
init|=
literal|"internal.am.tmp.dir"
decl_stmt|;
comment|/**    * internal temp directory: {@value}    */
DECL|field|INTERNAL_TMP_DIR
name|String
name|INTERNAL_TMP_DIR
init|=
literal|"internal.tmp.dir"
decl_stmt|;
comment|/**    * where a snapshot of the original conf dir is: {@value}    */
DECL|field|INTERNAL_SNAPSHOT_CONF_PATH
name|String
name|INTERNAL_SNAPSHOT_CONF_PATH
init|=
literal|"internal.snapshot.conf.path"
decl_stmt|;
comment|/**    * where a snapshot of the original conf dir is: {@value}    */
DECL|field|INTERNAL_GENERATED_CONF_PATH
name|String
name|INTERNAL_GENERATED_CONF_PATH
init|=
literal|"internal.generated.conf.path"
decl_stmt|;
comment|/**    * where a snapshot of the original conf dir is: {@value}    */
DECL|field|INTERNAL_PROVIDER_NAME
name|String
name|INTERNAL_PROVIDER_NAME
init|=
literal|"internal.provider.name"
decl_stmt|;
comment|/**    * where a snapshot of the original conf dir is: {@value}    */
DECL|field|INTERNAL_DATA_DIR_PATH
name|String
name|INTERNAL_DATA_DIR_PATH
init|=
literal|"internal.data.dir.path"
decl_stmt|;
comment|/**    * where the app def is stored    */
DECL|field|INTERNAL_APPDEF_DIR_PATH
name|String
name|INTERNAL_APPDEF_DIR_PATH
init|=
literal|"internal.appdef.dir.path"
decl_stmt|;
comment|/**    * where addons for the app are stored    */
DECL|field|INTERNAL_ADDONS_DIR_PATH
name|String
name|INTERNAL_ADDONS_DIR_PATH
init|=
literal|"internal.addons.dir.path"
decl_stmt|;
comment|/**    * Time in milliseconds to wait after forking any in-AM     * process before attempting to start up the containers: {@value}    *    * A shorter value brings the cluster up faster, but means that if the    * in AM process fails (due to a bad configuration), then time    * is wasted starting containers on a cluster that isn't going to come    * up    */
DECL|field|DEFAULT_INTERNAL_CONTAINER_STARTUP_DELAY
name|int
name|DEFAULT_INTERNAL_CONTAINER_STARTUP_DELAY
init|=
literal|5000
decl_stmt|;
comment|/**    * Time in seconds before a container is considered long-lived.    * Shortlived containers are interpreted as a problem with the role    * and/or the host: {@value}    */
DECL|field|INTERNAL_CONTAINER_FAILURE_SHORTLIFE
name|String
name|INTERNAL_CONTAINER_FAILURE_SHORTLIFE
init|=
literal|"internal.container.failure.shortlife"
decl_stmt|;
comment|/**    * Default short life threshold: {@value}    */
DECL|field|DEFAULT_INTERNAL_CONTAINER_FAILURE_SHORTLIFE
name|int
name|DEFAULT_INTERNAL_CONTAINER_FAILURE_SHORTLIFE
init|=
literal|60
decl_stmt|;
comment|/**    * Version of the app: {@value}    */
DECL|field|KEYTAB_LOCATION
name|String
name|KEYTAB_LOCATION
init|=
literal|"internal.keytab.location"
decl_stmt|;
comment|/**    * Queue used to deploy the app: {@value}    */
DECL|field|INTERNAL_QUEUE
name|String
name|INTERNAL_QUEUE
init|=
literal|"internal.queue"
decl_stmt|;
comment|/**    * Flag to indicate whether or not the chaos monkey is enabled:    * {@value}    */
DECL|field|CHAOS_MONKEY_ENABLED
name|String
name|CHAOS_MONKEY_ENABLED
init|=
literal|"internal.chaos.monkey.enabled"
decl_stmt|;
DECL|field|DEFAULT_CHAOS_MONKEY_ENABLED
name|boolean
name|DEFAULT_CHAOS_MONKEY_ENABLED
init|=
literal|false
decl_stmt|;
comment|/**    * Rate    */
DECL|field|CHAOS_MONKEY_INTERVAL
name|String
name|CHAOS_MONKEY_INTERVAL
init|=
literal|"internal.chaos.monkey.interval"
decl_stmt|;
DECL|field|CHAOS_MONKEY_INTERVAL_DAYS
name|String
name|CHAOS_MONKEY_INTERVAL_DAYS
init|=
name|CHAOS_MONKEY_INTERVAL
operator|+
literal|".days"
decl_stmt|;
DECL|field|CHAOS_MONKEY_INTERVAL_HOURS
name|String
name|CHAOS_MONKEY_INTERVAL_HOURS
init|=
name|CHAOS_MONKEY_INTERVAL
operator|+
literal|".hours"
decl_stmt|;
DECL|field|CHAOS_MONKEY_INTERVAL_MINUTES
name|String
name|CHAOS_MONKEY_INTERVAL_MINUTES
init|=
name|CHAOS_MONKEY_INTERVAL
operator|+
literal|".minutes"
decl_stmt|;
DECL|field|CHAOS_MONKEY_INTERVAL_SECONDS
name|String
name|CHAOS_MONKEY_INTERVAL_SECONDS
init|=
name|CHAOS_MONKEY_INTERVAL
operator|+
literal|".seconds"
decl_stmt|;
DECL|field|DEFAULT_CHAOS_MONKEY_INTERVAL_DAYS
name|long
name|DEFAULT_CHAOS_MONKEY_INTERVAL_DAYS
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_CHAOS_MONKEY_INTERVAL_HOURS
name|long
name|DEFAULT_CHAOS_MONKEY_INTERVAL_HOURS
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_CHAOS_MONKEY_INTERVAL_MINUTES
name|long
name|DEFAULT_CHAOS_MONKEY_INTERVAL_MINUTES
init|=
literal|0
decl_stmt|;
DECL|field|CHAOS_MONKEY_DELAY
name|String
name|CHAOS_MONKEY_DELAY
init|=
literal|"internal.chaos.monkey.delay"
decl_stmt|;
DECL|field|CHAOS_MONKEY_DELAY_DAYS
name|String
name|CHAOS_MONKEY_DELAY_DAYS
init|=
name|CHAOS_MONKEY_DELAY
operator|+
literal|".days"
decl_stmt|;
DECL|field|CHAOS_MONKEY_DELAY_HOURS
name|String
name|CHAOS_MONKEY_DELAY_HOURS
init|=
name|CHAOS_MONKEY_DELAY
operator|+
literal|".hours"
decl_stmt|;
DECL|field|CHAOS_MONKEY_DELAY_MINUTES
name|String
name|CHAOS_MONKEY_DELAY_MINUTES
init|=
name|CHAOS_MONKEY_DELAY
operator|+
literal|".minutes"
decl_stmt|;
DECL|field|CHAOS_MONKEY_DELAY_SECONDS
name|String
name|CHAOS_MONKEY_DELAY_SECONDS
init|=
name|CHAOS_MONKEY_DELAY
operator|+
literal|".seconds"
decl_stmt|;
DECL|field|DEFAULT_CHAOS_MONKEY_STARTUP_DELAY
name|int
name|DEFAULT_CHAOS_MONKEY_STARTUP_DELAY
init|=
literal|0
decl_stmt|;
comment|/**    * Prefix for all chaos monkey probabilities    */
DECL|field|CHAOS_MONKEY_PROBABILITY
name|String
name|CHAOS_MONKEY_PROBABILITY
init|=
literal|"internal.chaos.monkey.probability"
decl_stmt|;
comment|/**    * Probabilies are out of 10000 ; 100==1%    */
comment|/**    * Probability of a monkey check killing the AM:  {@value}    */
DECL|field|CHAOS_MONKEY_PROBABILITY_AM_FAILURE
name|String
name|CHAOS_MONKEY_PROBABILITY_AM_FAILURE
init|=
name|CHAOS_MONKEY_PROBABILITY
operator|+
literal|".amfailure"
decl_stmt|;
comment|/**    * Default probability of a monkey check killing the AM:  {@value}    */
DECL|field|DEFAULT_CHAOS_MONKEY_PROBABILITY_AM_FAILURE
name|int
name|DEFAULT_CHAOS_MONKEY_PROBABILITY_AM_FAILURE
init|=
literal|0
decl_stmt|;
comment|/**    * Probability of a monkey check killing the AM:  {@value}    */
DECL|field|CHAOS_MONKEY_PROBABILITY_AM_LAUNCH_FAILURE
name|String
name|CHAOS_MONKEY_PROBABILITY_AM_LAUNCH_FAILURE
init|=
name|CHAOS_MONKEY_PROBABILITY
operator|+
literal|".amlaunchfailure"
decl_stmt|;
comment|/**    * Probability of a monkey check killing a container:  {@value}    */
DECL|field|CHAOS_MONKEY_PROBABILITY_CONTAINER_FAILURE
name|String
name|CHAOS_MONKEY_PROBABILITY_CONTAINER_FAILURE
init|=
name|CHAOS_MONKEY_PROBABILITY
operator|+
literal|".containerfailure"
decl_stmt|;
comment|/**    * Default probability of a monkey check killing the a container:  {@value}    */
DECL|field|DEFAULT_CHAOS_MONKEY_PROBABILITY_CONTAINER_FAILURE
name|int
name|DEFAULT_CHAOS_MONKEY_PROBABILITY_CONTAINER_FAILURE
init|=
literal|0
decl_stmt|;
comment|/**    * 1% of chaos    */
DECL|field|PROBABILITY_PERCENT_1
name|int
name|PROBABILITY_PERCENT_1
init|=
literal|100
decl_stmt|;
comment|/**    * 100% for chaos values    */
DECL|field|PROBABILITY_PERCENT_100
name|int
name|PROBABILITY_PERCENT_100
init|=
literal|100
operator|*
name|PROBABILITY_PERCENT_1
decl_stmt|;
comment|/**    * interval between checks for escalation: {@value}    */
DECL|field|ESCALATION_CHECK_INTERVAL
name|String
name|ESCALATION_CHECK_INTERVAL
init|=
literal|"escalation.check.interval.seconds"
decl_stmt|;
comment|/**    * default value: {@value}    */
DECL|field|DEFAULT_ESCALATION_CHECK_INTERVAL
name|int
name|DEFAULT_ESCALATION_CHECK_INTERVAL
init|=
literal|30
decl_stmt|;
comment|/**    * interval between readiness checks: {@value}    */
DECL|field|MONITOR_INTERVAL
name|String
name|MONITOR_INTERVAL
init|=
literal|"monitor.interval.seconds"
decl_stmt|;
comment|/**    * default value: {@value}    */
DECL|field|DEFAULT_MONITOR_INTERVAL
name|int
name|DEFAULT_MONITOR_INTERVAL
init|=
literal|30
decl_stmt|;
block|}
end_interface

end_unit

