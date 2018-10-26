begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell
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
operator|.
name|ozShell
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|client
operator|.
name|rest
operator|.
name|OzoneException
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_comment
comment|/**  * Test ozone URL parsing.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestOzoneAddress
specifier|public
class|class
name|TestOzoneAddress
block|{
annotation|@
name|Parameters
DECL|method|data ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"o3fs://localhost:9878/"
block|}
block|,
block|{
literal|"o3fs://localhost/"
block|}
block|,
block|{
literal|"o3fs:///"
block|}
block|,
block|{
literal|"http://localhost:9878/"
block|}
block|,
block|{
literal|"http://localhost/"
block|}
block|,
block|{
literal|"http:///"
block|}
block|,
block|{
literal|"/"
block|}
block|}
argument_list|)
return|;
block|}
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|method|TestOzoneAddress (String prefix)
specifier|public
name|TestOzoneAddress
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkUrlTypes ()
specifier|public
name|void
name|checkUrlTypes
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|OzoneAddress
name|address
decl_stmt|;
name|address
operator|=
operator|new
name|OzoneAddress
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|address
operator|.
name|ensureRootAddress
argument_list|()
expr_stmt|;
name|address
operator|=
operator|new
name|OzoneAddress
argument_list|(
name|prefix
operator|+
literal|""
argument_list|)
expr_stmt|;
name|address
operator|.
name|ensureRootAddress
argument_list|()
expr_stmt|;
name|address
operator|=
operator|new
name|OzoneAddress
argument_list|(
name|prefix
operator|+
literal|"vol1"
argument_list|)
expr_stmt|;
name|address
operator|.
name|ensureVolumeAddress
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"vol1"
argument_list|,
name|address
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|address
operator|=
operator|new
name|OzoneAddress
argument_list|(
name|prefix
operator|+
literal|"vol1/bucket"
argument_list|)
expr_stmt|;
name|address
operator|.
name|ensureBucketAddress
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"vol1"
argument_list|,
name|address
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bucket"
argument_list|,
name|address
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|address
operator|=
operator|new
name|OzoneAddress
argument_list|(
name|prefix
operator|+
literal|"vol1/bucket/"
argument_list|)
expr_stmt|;
name|address
operator|.
name|ensureBucketAddress
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"vol1"
argument_list|,
name|address
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bucket"
argument_list|,
name|address
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|address
operator|=
operator|new
name|OzoneAddress
argument_list|(
name|prefix
operator|+
literal|"vol1/bucket/key"
argument_list|)
expr_stmt|;
name|address
operator|.
name|ensureKeyAddress
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"vol1"
argument_list|,
name|address
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bucket"
argument_list|,
name|address
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"key"
argument_list|,
name|address
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
name|address
operator|=
operator|new
name|OzoneAddress
argument_list|(
name|prefix
operator|+
literal|"vol1/bucket/key/"
argument_list|)
expr_stmt|;
name|address
operator|.
name|ensureKeyAddress
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"vol1"
argument_list|,
name|address
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bucket"
argument_list|,
name|address
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"key/"
argument_list|,
name|address
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
name|address
operator|=
operator|new
name|OzoneAddress
argument_list|(
name|prefix
operator|+
literal|"vol1/bucket/key1/key3/key"
argument_list|)
expr_stmt|;
name|address
operator|.
name|ensureKeyAddress
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"vol1"
argument_list|,
name|address
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bucket"
argument_list|,
name|address
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"key1/key3/key"
argument_list|,
name|address
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

