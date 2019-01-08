begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security.acl
package|package
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
package|;
end_package

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
name|*
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
name|OzoneObj
operator|.
name|ResourceType
import|;
end_import

begin_comment
comment|/**  * Test class for {@link OzoneObjInfo}.  * */
end_comment

begin_class
DECL|class|TestOzoneObjInfo
specifier|public
class|class
name|TestOzoneObjInfo
block|{
DECL|field|objInfo
specifier|private
name|OzoneObjInfo
name|objInfo
decl_stmt|;
DECL|field|builder
specifier|private
name|OzoneObjInfo
operator|.
name|Builder
name|builder
decl_stmt|;
DECL|field|volume
specifier|private
name|String
name|volume
init|=
literal|"vol1"
decl_stmt|;
DECL|field|bucket
specifier|private
name|String
name|bucket
init|=
literal|"bucket1"
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
DECL|field|STORE
specifier|private
specifier|static
specifier|final
name|OzoneObj
operator|.
name|StoreType
name|STORE
init|=
name|OzoneObj
operator|.
name|StoreType
operator|.
name|OZONE
decl_stmt|;
annotation|@
name|Test
DECL|method|testGetVolumeName ()
specifier|public
name|void
name|testGetVolumeName
parameter_list|()
block|{
name|builder
operator|=
name|getBuilder
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|objInfo
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|volume
argument_list|)
expr_stmt|;
name|objInfo
operator|=
name|getBuilder
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|objInfo
operator|=
name|getBuilder
argument_list|(
name|volume
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|volume
argument_list|)
expr_stmt|;
block|}
DECL|method|getBuilder (String withVolume, String withBucket, String withKey)
specifier|private
name|OzoneObjInfo
operator|.
name|Builder
name|getBuilder
parameter_list|(
name|String
name|withVolume
parameter_list|,
name|String
name|withBucket
parameter_list|,
name|String
name|withKey
parameter_list|)
block|{
return|return
name|OzoneObjInfo
operator|.
name|Builder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setResType
argument_list|(
name|ResourceType
operator|.
name|VOLUME
argument_list|)
operator|.
name|setStoreType
argument_list|(
name|STORE
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|withVolume
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|withBucket
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|withKey
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testGetBucketName ()
specifier|public
name|void
name|testGetBucketName
parameter_list|()
block|{
name|objInfo
operator|=
name|getBuilder
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|objInfo
operator|=
name|getBuilder
argument_list|(
name|volume
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getBucketName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|objInfo
operator|=
name|getBuilder
argument_list|(
literal|null
argument_list|,
name|bucket
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetKeyName ()
specifier|public
name|void
name|testGetKeyName
parameter_list|()
block|{
name|objInfo
operator|=
name|getBuilder
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getKeyName
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|objInfo
operator|=
name|getBuilder
argument_list|(
name|volume
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getKeyName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|objInfo
operator|=
name|getBuilder
argument_list|(
literal|null
argument_list|,
name|bucket
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getKeyName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|objInfo
operator|=
name|getBuilder
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|key
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|objInfo
operator|.
name|getKeyName
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

