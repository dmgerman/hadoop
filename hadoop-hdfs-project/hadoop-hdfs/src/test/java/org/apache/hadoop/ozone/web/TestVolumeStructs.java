begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
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
name|web
operator|.
name|response
operator|.
name|ListVolumes
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
name|web
operator|.
name|response
operator|.
name|VolumeInfo
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
name|web
operator|.
name|response
operator|.
name|VolumeOwner
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
name|IOException
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test Ozone Volume info structure.  */
end_comment

begin_class
DECL|class|TestVolumeStructs
specifier|public
class|class
name|TestVolumeStructs
block|{
annotation|@
name|Test
DECL|method|testVolumeInfoParse ()
specifier|public
name|void
name|testVolumeInfoParse
parameter_list|()
throws|throws
name|IOException
block|{
name|VolumeInfo
name|volInfo
init|=
operator|new
name|VolumeInfo
argument_list|(
literal|"testvol"
argument_list|,
literal|"Thu, Apr 9, 2015 10:23:45 GMT"
argument_list|,
literal|"gandalf"
argument_list|)
decl_stmt|;
name|VolumeOwner
name|owner
init|=
operator|new
name|VolumeOwner
argument_list|(
literal|"bilbo"
argument_list|)
decl_stmt|;
name|volInfo
operator|.
name|setOwner
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|String
name|jString
init|=
name|volInfo
operator|.
name|toJsonString
argument_list|()
decl_stmt|;
name|VolumeInfo
name|newVollInfo
init|=
name|VolumeInfo
operator|.
name|parse
argument_list|(
name|jString
argument_list|)
decl_stmt|;
name|String
name|one
init|=
name|volInfo
operator|.
name|toJsonString
argument_list|()
decl_stmt|;
name|String
name|two
init|=
name|newVollInfo
operator|.
name|toJsonString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|volInfo
operator|.
name|toJsonString
argument_list|()
argument_list|,
name|newVollInfo
operator|.
name|toJsonString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVolumeInfoValue ()
specifier|public
name|void
name|testVolumeInfoValue
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|createdOn
init|=
literal|"Thu, Apr 9, 2015 10:23:45 GMT"
decl_stmt|;
name|String
name|createdBy
init|=
literal|"gandalf"
decl_stmt|;
name|VolumeInfo
name|volInfo
init|=
operator|new
name|VolumeInfo
argument_list|(
literal|"testvol"
argument_list|,
name|createdOn
argument_list|,
name|createdBy
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|volInfo
operator|.
name|getCreatedBy
argument_list|()
argument_list|,
name|createdBy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|volInfo
operator|.
name|getCreatedOn
argument_list|()
argument_list|,
name|createdOn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVolumeListParse ()
specifier|public
name|void
name|testVolumeListParse
parameter_list|()
throws|throws
name|IOException
block|{
name|ListVolumes
name|list
init|=
operator|new
name|ListVolumes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|100
condition|;
name|x
operator|++
control|)
block|{
name|VolumeInfo
name|volInfo
init|=
operator|new
name|VolumeInfo
argument_list|(
literal|"testvol"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|x
argument_list|)
argument_list|,
literal|"Thu, Apr 9, 2015 10:23:45 GMT"
argument_list|,
literal|"gandalf"
argument_list|)
decl_stmt|;
name|list
operator|.
name|addVolume
argument_list|(
name|volInfo
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|sort
argument_list|()
expr_stmt|;
name|String
name|listString
init|=
name|list
operator|.
name|toJsonString
argument_list|()
decl_stmt|;
name|ListVolumes
name|newList
init|=
name|ListVolumes
operator|.
name|parse
argument_list|(
name|listString
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|list
operator|.
name|toJsonString
argument_list|()
argument_list|,
name|newList
operator|.
name|toJsonString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

