begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.subapplication
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
name|timelineservice
operator|.
name|storage
operator|.
name|subapplication
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
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|BaseTable
import|;
end_import

begin_comment
comment|/**  * The sub application table has column families:  * info, config and metrics.  * Info stores information about a timeline entity object  * config stores configuration data of a timeline entity object  * metrics stores the metrics of a timeline entity object  *  * Example sub application table record:  *  *<pre>  * |-------------------------------------------------------------------------|  * |  Row          | Column Family             | Column Family| Column Family|  * |  key          | info                      | metrics      | config       |  * |-------------------------------------------------------------------------|  * | subAppUserId! | id:entityId               | metricId1:   | configKey1:  |  * | clusterId!    | type:entityType           | metricValue1 | configValue1 |  * | entityType!   |                           | @timestamp1  |              |  * | idPrefix!|    |                           |              | configKey2:  |  * | entityId!     | created_time:             | metricId1:   | configValue2 |  * | userId        | 1392993084018             | metricValue2 |              |  * |               |                           | @timestamp2  |              |  * |               | i!infoKey:                |              |              |  * |               | infoValue                 | metricId1:   |              |  * |               |                           | metricValue1 |              |  * |               |                           | @timestamp2  |              |  * |               | e!eventId=timestamp=      |              |              |  * |               | infoKey:                  |              |              |  * |               | eventInfoValue            |              |              |  * |               |                           |              |              |  * |               | r!relatesToKey:           |              |              |  * |               | id3=id4=id5               |              |              |  * |               |                           |              |              |  * |               | s!isRelatedToKey          |              |              |  * |               | id7=id9=id6               |              |              |  * |               |                           |              |              |  * |               | flowVersion:              |              |              |  * |               | versionValue              |              |              |  * |-------------------------------------------------------------------------|  *</pre>  */
end_comment

begin_class
DECL|class|SubApplicationTable
specifier|public
specifier|final
class|class
name|SubApplicationTable
extends|extends
name|BaseTable
argument_list|<
name|SubApplicationTable
argument_list|>
block|{ }
end_class

end_unit

