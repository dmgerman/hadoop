begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|web
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|yarn
operator|.
name|sls
operator|.
name|SLSRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|TestSLSWebApp
specifier|public
class|class
name|TestSLSWebApp
block|{
annotation|@
name|Test
DECL|method|testSimulateInfoPageHtmlTemplate ()
specifier|public
name|void
name|testSimulateInfoPageHtmlTemplate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|simulateInfoTemplate
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
literal|"src/main/html/simulate.info.html.template"
argument_list|)
argument_list|)
decl_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Number of racks"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Number of nodes"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Node memory (MB)"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Node VCores"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Number of applications"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Number of tasks"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Average tasks per applicaion"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Number of queues"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Average applications per queue"
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|put
argument_list|(
literal|"Estimated simulate time (s)"
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|StringBuilder
name|info
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|info
operator|.
name|append
argument_list|(
literal|"<tr>"
argument_list|)
expr_stmt|;
name|info
operator|.
name|append
argument_list|(
literal|"<td class='td1'>"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"</td>"
argument_list|)
expr_stmt|;
name|info
operator|.
name|append
argument_list|(
literal|"<td class='td2'>"
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|"</td>"
argument_list|)
expr_stmt|;
name|info
operator|.
name|append
argument_list|(
literal|"</tr>"
argument_list|)
expr_stmt|;
block|}
name|String
name|simulateInfo
init|=
name|MessageFormat
operator|.
name|format
argument_list|(
name|simulateInfoTemplate
argument_list|,
name|info
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The simulate info html page should not be empty"
argument_list|,
name|simulateInfo
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|SLSRunner
operator|.
name|simulateInfoMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The simulate info html page should have information "
operator|+
literal|"of "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|simulateInfo
operator|.
name|contains
argument_list|(
literal|"<td class='td1'>"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"</td><td class='td2'>"
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|"</td>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSimulatePageHtmlTemplate ()
specifier|public
name|void
name|testSimulatePageHtmlTemplate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|simulateTemplate
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
literal|"src/main/html/simulate.html.template"
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|queues
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|queues
operator|.
name|add
argument_list|(
literal|"sls_queue_1"
argument_list|)
expr_stmt|;
name|queues
operator|.
name|add
argument_list|(
literal|"sls_queue_2"
argument_list|)
expr_stmt|;
name|queues
operator|.
name|add
argument_list|(
literal|"sls_queue_3"
argument_list|)
expr_stmt|;
name|String
name|queueInfo
init|=
literal|""
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|queue
range|:
name|queues
control|)
block|{
name|queueInfo
operator|+=
literal|"legends[4]["
operator|+
name|i
operator|+
literal|"] = 'queue"
operator|+
name|queue
operator|+
literal|".allocated.memory'"
expr_stmt|;
name|queueInfo
operator|+=
literal|"legends[5]["
operator|+
name|i
operator|+
literal|"] = 'queue"
operator|+
name|queue
operator|+
literal|".allocated.vcores'"
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|String
name|simulateInfo
init|=
name|MessageFormat
operator|.
name|format
argument_list|(
name|simulateTemplate
argument_list|,
name|queueInfo
argument_list|,
literal|"s"
argument_list|,
literal|1000
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The simulate page html page should not be empty"
argument_list|,
name|simulateInfo
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrackPageHtmlTemplate ()
specifier|public
name|void
name|testTrackPageHtmlTemplate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|trackTemplate
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
literal|"src/main/html/track.html.template"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|trackedQueueInfo
init|=
literal|""
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|trackedQueues
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|trackedQueues
operator|.
name|add
argument_list|(
literal|"sls_queue_1"
argument_list|)
expr_stmt|;
name|trackedQueues
operator|.
name|add
argument_list|(
literal|"sls_queue_2"
argument_list|)
expr_stmt|;
name|trackedQueues
operator|.
name|add
argument_list|(
literal|"sls_queue_3"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|queue
range|:
name|trackedQueues
control|)
block|{
name|trackedQueueInfo
operator|+=
literal|"<option value='Queue "
operator|+
name|queue
operator|+
literal|"'>"
operator|+
name|queue
operator|+
literal|"</option>"
expr_stmt|;
block|}
name|String
name|trackedAppInfo
init|=
literal|""
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|trackedApps
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|trackedApps
operator|.
name|add
argument_list|(
literal|"app_1"
argument_list|)
expr_stmt|;
name|trackedApps
operator|.
name|add
argument_list|(
literal|"app_2"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|job
range|:
name|trackedApps
control|)
block|{
name|trackedAppInfo
operator|+=
literal|"<option value='Job "
operator|+
name|job
operator|+
literal|"'>"
operator|+
name|job
operator|+
literal|"</option>"
expr_stmt|;
block|}
name|String
name|trackInfo
init|=
name|MessageFormat
operator|.
name|format
argument_list|(
name|trackTemplate
argument_list|,
name|trackedQueueInfo
argument_list|,
name|trackedAppInfo
argument_list|,
literal|"s"
argument_list|,
literal|1000
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The queue/app tracking html page should not be empty"
argument_list|,
name|trackInfo
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

