begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
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
name|utils
operator|.
name|db
operator|.
name|DBProfile
import|;
end_import

begin_comment
comment|/**  * This class contains constants for configuration keys and default values  * used in hdds.  */
end_comment

begin_class
DECL|class|HddsConfigKeys
specifier|public
specifier|final
class|class
name|HddsConfigKeys
block|{
comment|/**    * Do not instantiate.    */
DECL|method|HddsConfigKeys ()
specifier|private
name|HddsConfigKeys
parameter_list|()
block|{   }
DECL|field|HDDS_HEARTBEAT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_HEARTBEAT_INTERVAL
init|=
literal|"hdds.heartbeat.interval"
decl_stmt|;
DECL|field|HDDS_HEARTBEAT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_HEARTBEAT_INTERVAL_DEFAULT
init|=
literal|"30s"
decl_stmt|;
DECL|field|HDDS_NODE_REPORT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_NODE_REPORT_INTERVAL
init|=
literal|"hdds.node.report.interval"
decl_stmt|;
DECL|field|HDDS_NODE_REPORT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_NODE_REPORT_INTERVAL_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|HDDS_CONTAINER_REPORT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_CONTAINER_REPORT_INTERVAL
init|=
literal|"hdds.container.report.interval"
decl_stmt|;
DECL|field|HDDS_CONTAINER_REPORT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_CONTAINER_REPORT_INTERVAL_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|HDDS_COMMAND_STATUS_REPORT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_COMMAND_STATUS_REPORT_INTERVAL
init|=
literal|"hdds.command.status.report.interval"
decl_stmt|;
DECL|field|HDDS_COMMAND_STATUS_REPORT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_COMMAND_STATUS_REPORT_INTERVAL_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|HDDS_CONTAINER_ACTION_MAX_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_CONTAINER_ACTION_MAX_LIMIT
init|=
literal|"hdds.container.action.max.limit"
decl_stmt|;
DECL|field|HDDS_CONTAINER_ACTION_MAX_LIMIT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_CONTAINER_ACTION_MAX_LIMIT_DEFAULT
init|=
literal|20
decl_stmt|;
DECL|field|HDDS_PIPELINE_ACTION_MAX_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PIPELINE_ACTION_MAX_LIMIT
init|=
literal|"hdds.pipeline.action.max.limit"
decl_stmt|;
DECL|field|HDDS_PIPELINE_ACTION_MAX_LIMIT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_PIPELINE_ACTION_MAX_LIMIT_DEFAULT
init|=
literal|20
decl_stmt|;
comment|// Configuration to allow volume choosing policy.
DECL|field|HDDS_DATANODE_VOLUME_CHOOSING_POLICY
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_DATANODE_VOLUME_CHOOSING_POLICY
init|=
literal|"hdds.datanode.volume.choosing.policy"
decl_stmt|;
comment|// DB Profiles used by ROCKDB instances.
DECL|field|HDDS_DB_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_DB_PROFILE
init|=
literal|"hdds.db.profile"
decl_stmt|;
DECL|field|HDDS_DEFAULT_DB_PROFILE
specifier|public
specifier|static
specifier|final
name|DBProfile
name|HDDS_DEFAULT_DB_PROFILE
init|=
name|DBProfile
operator|.
name|SSD
decl_stmt|;
comment|// Once a container usage crosses this threshold, it is eligible for
comment|// closing.
DECL|field|HDDS_CONTAINER_CLOSE_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_CONTAINER_CLOSE_THRESHOLD
init|=
literal|"hdds.container.close.threshold"
decl_stmt|;
DECL|field|HDDS_CONTAINER_CLOSE_THRESHOLD_DEFAULT
specifier|public
specifier|static
specifier|final
name|float
name|HDDS_CONTAINER_CLOSE_THRESHOLD_DEFAULT
init|=
literal|0.9f
decl_stmt|;
block|}
end_class

end_unit

