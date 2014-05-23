begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.timeline.security
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
name|applicationhistoryservice
operator|.
name|timeline
operator|.
name|security
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
name|security
operator|.
name|UserGroupInformation
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
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineEntity
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
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|applicationhistoryservice
operator|.
name|timeline
operator|.
name|TimelineStore
import|;
end_import

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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestTimelineACLsManager
specifier|public
class|class
name|TestTimelineACLsManager
block|{
annotation|@
name|Test
DECL|method|testYarnACLsNotEnabled ()
specifier|public
name|void
name|testYarnACLsNotEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TimelineACLsManager
name|timelineACLsManager
init|=
operator|new
name|TimelineACLsManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|addPrimaryFilter
argument_list|(
name|TimelineStore
operator|.
name|SystemFilter
operator|.
name|ENTITY_OWNER
operator|.
name|toString
argument_list|()
argument_list|,
literal|"owner"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Always true when ACLs are not enabled"
argument_list|,
name|timelineACLsManager
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"user"
argument_list|)
argument_list|,
name|entity
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testYarnACLsEnabled ()
specifier|public
name|void
name|testYarnACLsEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TimelineACLsManager
name|timelineACLsManager
init|=
operator|new
name|TimelineACLsManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|addPrimaryFilter
argument_list|(
name|TimelineStore
operator|.
name|SystemFilter
operator|.
name|ENTITY_OWNER
operator|.
name|toString
argument_list|()
argument_list|,
literal|"owner"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Owner should be allowed to access"
argument_list|,
name|timelineACLsManager
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"owner"
argument_list|)
argument_list|,
name|entity
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Other shouldn't be allowed to access"
argument_list|,
name|timelineACLsManager
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"other"
argument_list|)
argument_list|,
name|entity
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCorruptedOwnerInfo ()
specifier|public
name|void
name|testCorruptedOwnerInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TimelineACLsManager
name|timelineACLsManager
init|=
operator|new
name|TimelineACLsManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
try|try
block|{
name|timelineACLsManager
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"owner"
argument_list|)
argument_list|,
name|entity
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"It's not the exact expected exception"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is corrupted."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

