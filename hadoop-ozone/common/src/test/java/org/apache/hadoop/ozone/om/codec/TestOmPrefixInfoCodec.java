begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.codec
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|codec
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
name|ozone
operator|.
name|OzoneAcl
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmPrefixInfo
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
name|ozone
operator|.
name|security
operator|.
name|acl
operator|.
name|IAccessAuthorizer
operator|.
name|ACLIdentityType
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
name|ozone
operator|.
name|security
operator|.
name|acl
operator|.
name|IAccessAuthorizer
operator|.
name|ACLType
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
name|test
operator|.
name|GenericTestUtils
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
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneAcl
operator|.
name|AclScope
operator|.
name|ACCESS
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
name|fail
import|;
end_import

begin_comment
comment|/**  * This class test OmPrefixInfoCodec.  */
end_comment

begin_class
DECL|class|TestOmPrefixInfoCodec
specifier|public
class|class
name|TestOmPrefixInfoCodec
block|{
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|field|codec
specifier|private
name|OmPrefixInfoCodec
name|codec
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|codec
operator|=
operator|new
name|OmPrefixInfoCodec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodecWithIncorrectValues ()
specifier|public
name|void
name|testCodecWithIncorrectValues
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|codec
operator|.
name|fromPersistedFormat
argument_list|(
literal|"random"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testCodecWithIncorrectValues failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Can't encode the the raw "
operator|+
literal|"data from the byte array"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCodecWithNullDataFromTable ()
specifier|public
name|void
name|testCodecWithNullDataFromTable
parameter_list|()
throws|throws
name|Exception
block|{
name|thrown
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|codec
operator|.
name|fromPersistedFormat
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodecWithNullDataFromUser ()
specifier|public
name|void
name|testCodecWithNullDataFromUser
parameter_list|()
throws|throws
name|Exception
block|{
name|thrown
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|codec
operator|.
name|toPersistedFormat
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToAndFromPersistedFormat ()
specifier|public
name|void
name|testToAndFromPersistedFormat
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|OzoneAcl
name|ozoneAcl
init|=
operator|new
name|OzoneAcl
argument_list|(
name|ACLIdentityType
operator|.
name|USER
argument_list|,
literal|"hive"
argument_list|,
name|ACLType
operator|.
name|ALL
argument_list|,
name|ACCESS
argument_list|)
decl_stmt|;
name|acls
operator|.
name|add
argument_list|(
name|ozoneAcl
argument_list|)
expr_stmt|;
name|OmPrefixInfo
name|opiSave
init|=
name|OmPrefixInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"/user/hive/warehouse"
argument_list|)
operator|.
name|setAcls
argument_list|(
name|acls
argument_list|)
operator|.
name|addMetadata
argument_list|(
literal|"id"
argument_list|,
literal|"100"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmPrefixInfo
name|opiLoad
init|=
name|codec
operator|.
name|fromPersistedFormat
argument_list|(
name|codec
operator|.
name|toPersistedFormat
argument_list|(
name|opiSave
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Load saved prefix info should match"
argument_list|,
name|opiLoad
operator|.
name|equals
argument_list|(
name|opiSave
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

