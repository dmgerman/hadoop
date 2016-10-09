begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
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

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|interface|YarnWebParams
specifier|public
interface|interface
name|YarnWebParams
block|{
DECL|field|RM_WEB_UI
specifier|static
specifier|final
name|String
name|RM_WEB_UI
init|=
literal|"ResourceManager"
decl_stmt|;
DECL|field|APP_HISTORY_WEB_UI
specifier|static
specifier|final
name|String
name|APP_HISTORY_WEB_UI
init|=
literal|"ApplicationHistoryServer"
decl_stmt|;
DECL|field|NM_NODENAME
name|String
name|NM_NODENAME
init|=
literal|"nm.id"
decl_stmt|;
DECL|field|APPLICATION_ID
name|String
name|APPLICATION_ID
init|=
literal|"app.id"
decl_stmt|;
DECL|field|APPLICATION_ATTEMPT_ID
name|String
name|APPLICATION_ATTEMPT_ID
init|=
literal|"appattempt.id"
decl_stmt|;
DECL|field|CONTAINER_ID
name|String
name|CONTAINER_ID
init|=
literal|"container.id"
decl_stmt|;
DECL|field|CONTAINER_LOG_TYPE
name|String
name|CONTAINER_LOG_TYPE
init|=
literal|"log.type"
decl_stmt|;
DECL|field|ENTITY_STRING
name|String
name|ENTITY_STRING
init|=
literal|"entity.string"
decl_stmt|;
DECL|field|APP_OWNER
name|String
name|APP_OWNER
init|=
literal|"app.owner"
decl_stmt|;
DECL|field|APP_STATE
name|String
name|APP_STATE
init|=
literal|"app.state"
decl_stmt|;
DECL|field|APP_START_TIME_BEGIN
name|String
name|APP_START_TIME_BEGIN
init|=
literal|"app.started-time.begin"
decl_stmt|;
DECL|field|APP_START_TIME_END
name|String
name|APP_START_TIME_END
init|=
literal|"app.started-time.end"
decl_stmt|;
DECL|field|APPS_NUM
name|String
name|APPS_NUM
init|=
literal|"apps.num"
decl_stmt|;
DECL|field|QUEUE_NAME
name|String
name|QUEUE_NAME
init|=
literal|"queue.name"
decl_stmt|;
DECL|field|NODE_STATE
name|String
name|NODE_STATE
init|=
literal|"node.state"
decl_stmt|;
DECL|field|NODE_LABEL
name|String
name|NODE_LABEL
init|=
literal|"node.label"
decl_stmt|;
DECL|field|WEB_UI_TYPE
name|String
name|WEB_UI_TYPE
init|=
literal|"web.ui.type"
decl_stmt|;
DECL|field|NEXT_REFRESH_INTERVAL
name|String
name|NEXT_REFRESH_INTERVAL
init|=
literal|"next.refresh.interval"
decl_stmt|;
DECL|field|ERROR_MESSAGE
name|String
name|ERROR_MESSAGE
init|=
literal|"error.message"
decl_stmt|;
block|}
end_interface

end_unit

