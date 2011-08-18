begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|resourcemanager
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_class
DECL|class|RMConfig
specifier|public
class|class
name|RMConfig
block|{
DECL|field|RM_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|RM_KEYTAB
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"keytab"
decl_stmt|;
DECL|field|ZK_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|ZK_ADDRESS
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"zookeeper.address"
decl_stmt|;
DECL|field|ZK_SESSION_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|ZK_SESSION_TIMEOUT
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"zookeeper.session.timeout"
decl_stmt|;
DECL|field|ADMIN_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_ADDRESS
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"admin.address"
decl_stmt|;
DECL|field|AM_MAX_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|AM_MAX_RETRIES
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"application.max.retries"
decl_stmt|;
DECL|field|DEFAULT_ZK_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_ZK_TIMEOUT
init|=
literal|60000
decl_stmt|;
DECL|field|DEFAULT_AM_MAX_RETRIES
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_AM_MAX_RETRIES
init|=
literal|3
decl_stmt|;
DECL|field|DEFAULT_AM_EXPIRY_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_AM_EXPIRY_INTERVAL
init|=
literal|600000
decl_stmt|;
DECL|field|NM_EXPIRY_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|NM_EXPIRY_INTERVAL
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"nodemanager.expiry.interval"
decl_stmt|;
DECL|field|DEFAULT_NM_EXPIRY_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_EXPIRY_INTERVAL
init|=
literal|600000
decl_stmt|;
DECL|field|DEFAULT_ADMIN_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_ADMIN_BIND_ADDRESS
init|=
literal|"0.0.0.0:8141"
decl_stmt|;
DECL|field|RESOURCE_SCHEDULER
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_SCHEDULER
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"scheduler"
decl_stmt|;
DECL|field|RM_STORE
specifier|public
specifier|static
specifier|final
name|String
name|RM_STORE
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"store"
decl_stmt|;
DECL|field|AMLIVELINESS_MONITORING_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|AMLIVELINESS_MONITORING_INTERVAL
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"amliveliness-monitor.monitoring-interval"
decl_stmt|;
DECL|field|DEFAULT_AMLIVELINESS_MONITORING_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_AMLIVELINESS_MONITORING_INTERVAL
init|=
literal|1000
decl_stmt|;
DECL|field|CONTAINER_LIVELINESS_MONITORING_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_LIVELINESS_MONITORING_INTERVAL
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"amliveliness-monitor.monitoring-interval"
decl_stmt|;
DECL|field|DEFAULT_CONTAINER_LIVELINESS_MONITORING_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CONTAINER_LIVELINESS_MONITORING_INTERVAL
init|=
literal|600000
decl_stmt|;
DECL|field|NMLIVELINESS_MONITORING_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|NMLIVELINESS_MONITORING_INTERVAL
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"nmliveliness-monitor.monitoring-interval"
decl_stmt|;
DECL|field|DEFAULT_NMLIVELINESS_MONITORING_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NMLIVELINESS_MONITORING_INTERVAL
init|=
literal|1000
decl_stmt|;
DECL|field|RM_RESOURCE_TRACKER_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|RM_RESOURCE_TRACKER_THREADS
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"resource.tracker.threads"
decl_stmt|;
DECL|field|DEFAULT_RM_RESOURCE_TRACKER_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_RESOURCE_TRACKER_THREADS
init|=
literal|10
decl_stmt|;
DECL|field|RM_CLIENT_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|RM_CLIENT_THREADS
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"client.threads"
decl_stmt|;
DECL|field|DEFAULT_RM_CLIENT_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_CLIENT_THREADS
init|=
literal|10
decl_stmt|;
DECL|field|RM_AM_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|RM_AM_THREADS
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"am.threads"
decl_stmt|;
DECL|field|DEFAULT_RM_AM_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_AM_THREADS
init|=
literal|10
decl_stmt|;
DECL|field|RM_ADMIN_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|RM_ADMIN_THREADS
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"admin.threads"
decl_stmt|;
DECL|field|DEFAULT_RM_ADMIN_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_ADMIN_THREADS
init|=
literal|1
decl_stmt|;
comment|/* key for looking up the acls configuration for acls checking for application */
DECL|field|RM_ACLS_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|RM_ACLS_ENABLED
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"acls.enabled"
decl_stmt|;
DECL|field|RM_ADMIN_ACL
specifier|public
specifier|static
specifier|final
name|String
name|RM_ADMIN_ACL
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"admin.acl"
decl_stmt|;
DECL|field|DEFAULT_RM_ADMIN_ACL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_ADMIN_ACL
init|=
literal|"*"
decl_stmt|;
DECL|field|RM_NODES_INCLUDE_FILE
specifier|public
specifier|static
specifier|final
name|String
name|RM_NODES_INCLUDE_FILE
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"nodes.include"
decl_stmt|;
DECL|field|DEFAULT_RM_NODES_INCLUDE_FILE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_NODES_INCLUDE_FILE
init|=
literal|""
decl_stmt|;
DECL|field|RM_NODES_EXCLUDE_FILE
specifier|public
specifier|static
specifier|final
name|String
name|RM_NODES_EXCLUDE_FILE
init|=
name|YarnConfiguration
operator|.
name|RM_PREFIX
operator|+
literal|"nodes.exclude"
decl_stmt|;
DECL|field|DEFAULT_RM_NODES_EXCLUDE_FILE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_NODES_EXCLUDE_FILE
init|=
literal|""
decl_stmt|;
block|}
end_class

end_unit

