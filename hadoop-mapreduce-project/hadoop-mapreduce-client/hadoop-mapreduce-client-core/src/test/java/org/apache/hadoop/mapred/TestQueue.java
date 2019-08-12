begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Iterator
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileUtil
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * TestCounters checks the sanity and recoverability of Queue  */
end_comment

begin_class
DECL|class|TestQueue
specifier|public
class|class
name|TestQueue
block|{
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
name|TestJobConf
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|testDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * test QueueManager    * configuration from file    *     * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testQueue ()
specifier|public
name|void
name|testQueue
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f
init|=
literal|null
decl_stmt|;
try|try
block|{
name|f
operator|=
name|writeFile
argument_list|()
expr_stmt|;
name|QueueManager
name|manager
init|=
operator|new
name|QueueManager
argument_list|(
name|f
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|manager
operator|.
name|setSchedulerInfo
argument_list|(
literal|"first"
argument_list|,
literal|"queueInfo"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setSchedulerInfo
argument_list|(
literal|"second"
argument_list|,
literal|"queueInfoqueueInfo"
argument_list|)
expr_stmt|;
name|Queue
name|root
init|=
name|manager
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|root
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Queue
argument_list|>
name|iterator
init|=
name|root
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Queue
name|firstSubQueue
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"first"
argument_list|,
name|firstSubQueue
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|firstSubQueue
operator|.
name|getAcls
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapred.queue.first.acl-submit-job"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Users [user1, user2] and members of the groups [group1, group2] are allowed"
argument_list|)
expr_stmt|;
name|Queue
name|secondSubQueue
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"second"
argument_list|,
name|secondSubQueue
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondSubQueue
operator|.
name|getProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"key"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondSubQueue
operator|.
name|getProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"value1"
argument_list|)
expr_stmt|;
comment|// test status
name|assertThat
argument_list|(
name|firstSubQueue
operator|.
name|getState
argument_list|()
operator|.
name|getStateName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"running"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondSubQueue
operator|.
name|getState
argument_list|()
operator|.
name|getStateName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"stopped"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|template
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|template
operator|.
name|add
argument_list|(
literal|"first"
argument_list|)
expr_stmt|;
name|template
operator|.
name|add
argument_list|(
literal|"second"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|manager
operator|.
name|getLeafQueueNames
argument_list|()
argument_list|,
name|template
argument_list|)
expr_stmt|;
comment|// test user access
name|UserGroupInformation
name|mockUGI
init|=
name|mock
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockUGI
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"user1"
argument_list|)
expr_stmt|;
name|String
index|[]
name|groups
init|=
block|{
literal|"group1"
block|}
decl_stmt|;
name|when
argument_list|(
name|mockUGI
operator|.
name|getGroupNames
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|manager
operator|.
name|hasAccess
argument_list|(
literal|"first"
argument_list|,
name|QueueACL
operator|.
name|SUBMIT_JOB
argument_list|,
name|mockUGI
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|manager
operator|.
name|hasAccess
argument_list|(
literal|"second"
argument_list|,
name|QueueACL
operator|.
name|SUBMIT_JOB
argument_list|,
name|mockUGI
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|manager
operator|.
name|hasAccess
argument_list|(
literal|"first"
argument_list|,
name|QueueACL
operator|.
name|ADMINISTER_JOBS
argument_list|,
name|mockUGI
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockUGI
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"user3"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|manager
operator|.
name|hasAccess
argument_list|(
literal|"first"
argument_list|,
name|QueueACL
operator|.
name|ADMINISTER_JOBS
argument_list|,
name|mockUGI
argument_list|)
argument_list|)
expr_stmt|;
name|QueueAclsInfo
index|[]
name|qai
init|=
name|manager
operator|.
name|getQueueAcls
argument_list|(
name|mockUGI
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|qai
operator|.
name|length
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// test refresh queue
name|manager
operator|.
name|refreshQueues
argument_list|(
name|getConfiguration
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|root
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|Queue
name|firstSubQueue1
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Queue
name|secondSubQueue1
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// tets equal method
name|assertThat
argument_list|(
name|firstSubQueue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|firstSubQueue1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|firstSubQueue1
operator|.
name|getState
argument_list|()
operator|.
name|getStateName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"running"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondSubQueue1
operator|.
name|getState
argument_list|()
operator|.
name|getStateName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"stopped"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|firstSubQueue1
operator|.
name|getSchedulingInfo
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"queueInfo"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondSubQueue1
operator|.
name|getSchedulingInfo
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"queueInfoqueueInfo"
argument_list|)
expr_stmt|;
comment|// test JobQueueInfo
name|assertThat
argument_list|(
name|firstSubQueue
operator|.
name|getJobQueueInfo
argument_list|()
operator|.
name|getQueueName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"first"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|firstSubQueue
operator|.
name|getJobQueueInfo
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"running"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|firstSubQueue
operator|.
name|getJobQueueInfo
argument_list|()
operator|.
name|getSchedulingInfo
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"queueInfo"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondSubQueue
operator|.
name|getJobQueueInfo
argument_list|()
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// test
name|assertThat
argument_list|(
name|manager
operator|.
name|getSchedulerInfo
argument_list|(
literal|"first"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"queueInfo"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|queueJobQueueInfos
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|JobQueueInfo
name|jobInfo
range|:
name|manager
operator|.
name|getJobQueueInfos
argument_list|()
control|)
block|{
name|queueJobQueueInfos
operator|.
name|add
argument_list|(
name|jobInfo
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|rootJobQueueInfos
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Queue
name|queue
range|:
name|root
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|rootJobQueueInfos
operator|.
name|add
argument_list|(
name|queue
operator|.
name|getJobQueueInfo
argument_list|()
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|queueJobQueueInfos
argument_list|,
name|rootJobQueueInfos
argument_list|)
expr_stmt|;
comment|// test getJobQueueInfoMapping
name|assertThat
argument_list|(
name|manager
operator|.
name|getJobQueueInfoMapping
argument_list|()
operator|.
name|get
argument_list|(
literal|"first"
argument_list|)
operator|.
name|getQueueName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"first"
argument_list|)
expr_stmt|;
comment|// test dumpConfiguration
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|unset
argument_list|(
name|DeprecatedQueueConfigurationParser
operator|.
name|MAPRED_QUEUE_NAMES_KEY
argument_list|)
expr_stmt|;
name|QueueManager
operator|.
name|dumpConfiguration
argument_list|(
name|writer
argument_list|,
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|String
name|result
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"\"name\":\"first\",\"state\":\"running\",\"acl_submit_job\":\"user1,user2 group1,group2\",\"acl_administer_jobs\":\"user3,user4 group3,group4\",\"properties\":[],\"children\":[]"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|QueueManager
operator|.
name|dumpConfiguration
argument_list|(
name|writer
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|result
operator|=
name|writer
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"{\"queues\":[{\"name\":\"default\",\"state\":\"running\",\"acl_submit_job\":\"*\",\"acl_administer_jobs\":\"*\",\"properties\":[],\"children\":[]},{\"name\":\"q1\",\"state\":\"running\",\"acl_submit_job\":\" \",\"acl_administer_jobs\":\" \",\"properties\":[],\"children\":[{\"name\":\"q1:q2\",\"state\":\"running\",\"acl_submit_job\":\" \",\"acl_administer_jobs\":\" \",\"properties\":["
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"{\"key\":\"capacity\",\"value\":\"20\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"{\"key\":\"user-limit\",\"value\":\"30\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"],\"children\":[]}]}]}"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test constructor QueueAclsInfo
name|QueueAclsInfo
name|qi
init|=
operator|new
name|QueueAclsInfo
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|qi
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getConfiguration ()
specifier|private
name|Configuration
name|getConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DeprecatedQueueConfigurationParser
operator|.
name|MAPRED_QUEUE_NAMES_KEY
argument_list|,
literal|"first,second"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|QueueManager
operator|.
name|QUEUE_CONF_PROPERTY_NAME_PREFIX
operator|+
literal|"first.acl-submit-job"
argument_list|,
literal|"user1,user2 group1,group2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|QueueManager
operator|.
name|QUEUE_CONF_PROPERTY_NAME_PREFIX
operator|+
literal|"first.state"
argument_list|,
literal|"running"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|QueueManager
operator|.
name|QUEUE_CONF_PROPERTY_NAME_PREFIX
operator|+
literal|"second.state"
argument_list|,
literal|"stopped"
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testDefaultConfig ()
specifier|public
name|void
name|testDefaultConfig
parameter_list|()
block|{
name|QueueManager
name|manager
init|=
operator|new
name|QueueManager
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|manager
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * test for Qmanager with empty configuration    *     * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|test2Queue ()
specifier|public
name|void
name|test2Queue
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|QueueManager
name|manager
init|=
operator|new
name|QueueManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|manager
operator|.
name|setSchedulerInfo
argument_list|(
literal|"first"
argument_list|,
literal|"queueInfo"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setSchedulerInfo
argument_list|(
literal|"second"
argument_list|,
literal|"queueInfoqueueInfo"
argument_list|)
expr_stmt|;
name|Queue
name|root
init|=
name|manager
operator|.
name|getRoot
argument_list|()
decl_stmt|;
comment|// test children queues
name|assertTrue
argument_list|(
name|root
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Queue
argument_list|>
name|iterator
init|=
name|root
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Queue
name|firstSubQueue
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"first"
argument_list|,
name|firstSubQueue
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|firstSubQueue
operator|.
name|getAcls
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapred.queue.first.acl-submit-job"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Users [user1, user2] and members of "
operator|+
literal|"the groups [group1, group2] are allowed"
argument_list|)
expr_stmt|;
name|Queue
name|secondSubQueue
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"second"
argument_list|,
name|secondSubQueue
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|firstSubQueue
operator|.
name|getState
argument_list|()
operator|.
name|getStateName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"running"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondSubQueue
operator|.
name|getState
argument_list|()
operator|.
name|getStateName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"stopped"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|manager
operator|.
name|isRunning
argument_list|(
literal|"first"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|manager
operator|.
name|isRunning
argument_list|(
literal|"second"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|firstSubQueue
operator|.
name|getSchedulingInfo
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"queueInfo"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondSubQueue
operator|.
name|getSchedulingInfo
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"queueInfoqueueInfo"
argument_list|)
expr_stmt|;
comment|// test leaf queue
name|Set
argument_list|<
name|String
argument_list|>
name|template
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|template
operator|.
name|add
argument_list|(
literal|"first"
argument_list|)
expr_stmt|;
name|template
operator|.
name|add
argument_list|(
literal|"second"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|manager
operator|.
name|getLeafQueueNames
argument_list|()
argument_list|,
name|template
argument_list|)
expr_stmt|;
block|}
comment|/**  * write cofiguration  * @return  * @throws IOException  */
DECL|method|writeFile ()
specifier|private
name|File
name|writeFile
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"tst.xml"
argument_list|)
decl_stmt|;
name|BufferedWriter
name|out
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|properties
init|=
literal|"<properties><property key=\"key\" value=\"value\"/><property key=\"key1\" value=\"value1\"/></properties>"
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<queues>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<queue><name>first</name><acl-submit-job>user1,user2 group1,group2</acl-submit-job><acl-administer-jobs>user3,user4 group3,group4</acl-administer-jobs><state>running</state></queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<queue><name>second</name><acl-submit-job>u1,u2 g1,g2</acl-submit-job>"
operator|+
name|properties
operator|+
literal|"<state>stopped</state></queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</queues>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
block|}
end_class

end_unit

