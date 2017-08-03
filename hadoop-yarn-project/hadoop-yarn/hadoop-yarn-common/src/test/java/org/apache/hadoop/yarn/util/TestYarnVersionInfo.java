begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|assertNotEquals
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
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * A JUnit test to test {@link YarnVersionInfo}  */
end_comment

begin_class
DECL|class|TestYarnVersionInfo
specifier|public
class|class
name|TestYarnVersionInfo
block|{
comment|/**    * Test the yarn version info routines.    * @throws IOException    */
annotation|@
name|Test
DECL|method|versionInfoGenerated ()
specifier|public
name|void
name|versionInfoGenerated
parameter_list|()
throws|throws
name|IOException
block|{
comment|// can't easily know what the correct values are going to be so just
comment|// make sure they aren't Unknown
name|assertNotEquals
argument_list|(
literal|"getVersion returned Unknown"
argument_list|,
literal|"Unknown"
argument_list|,
name|YarnVersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|"getUser returned Unknown"
argument_list|,
literal|"Unknown"
argument_list|,
name|YarnVersionInfo
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|"getSrcChecksum returned Unknown"
argument_list|,
literal|"Unknown"
argument_list|,
name|YarnVersionInfo
operator|.
name|getSrcChecksum
argument_list|()
argument_list|)
expr_stmt|;
comment|// these could be Unknown if the VersionInfo generated from code not in svn or git
comment|// so just check that they return something
name|assertNotNull
argument_list|(
literal|"getUrl returned null"
argument_list|,
name|YarnVersionInfo
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"getRevision returned null"
argument_list|,
name|YarnVersionInfo
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"getBranch returned null"
argument_list|,
name|YarnVersionInfo
operator|.
name|getBranch
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"getBuildVersion check doesn't contain: source checksum"
argument_list|,
name|YarnVersionInfo
operator|.
name|getBuildVersion
argument_list|()
operator|.
name|contains
argument_list|(
literal|"source checksum"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

