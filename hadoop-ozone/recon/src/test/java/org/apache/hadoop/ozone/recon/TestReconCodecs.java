begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
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
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|api
operator|.
name|types
operator|.
name|ContainerKeyPrefix
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
name|recon
operator|.
name|spi
operator|.
name|impl
operator|.
name|ContainerKeyPrefixCodec
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
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|Codec
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
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|IntegerCodec
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

begin_comment
comment|/**  * Unit Tests for Codecs used in Recon.  */
end_comment

begin_class
DECL|class|TestReconCodecs
specifier|public
class|class
name|TestReconCodecs
block|{
annotation|@
name|Test
DECL|method|testContainerKeyPrefixCodec ()
specifier|public
name|void
name|testContainerKeyPrefixCodec
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerKeyPrefix
name|containerKeyPrefix
init|=
operator|new
name|ContainerKeyPrefix
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|"TestKeyPrefix"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Codec
argument_list|<
name|ContainerKeyPrefix
argument_list|>
name|codec
init|=
operator|new
name|ContainerKeyPrefixCodec
argument_list|()
decl_stmt|;
name|byte
index|[]
name|persistedFormat
init|=
name|codec
operator|.
name|toPersistedFormat
argument_list|(
name|containerKeyPrefix
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|persistedFormat
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ContainerKeyPrefix
name|fromPersistedFormat
init|=
name|codec
operator|.
name|fromPersistedFormat
argument_list|(
name|persistedFormat
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerKeyPrefix
argument_list|,
name|fromPersistedFormat
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntegerCodec ()
specifier|public
name|void
name|testIntegerCodec
parameter_list|()
throws|throws
name|IOException
block|{
name|Integer
name|i
init|=
literal|1000
decl_stmt|;
name|Codec
argument_list|<
name|Integer
argument_list|>
name|codec
init|=
operator|new
name|IntegerCodec
argument_list|()
decl_stmt|;
name|byte
index|[]
name|persistedFormat
init|=
name|codec
operator|.
name|toPersistedFormat
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|persistedFormat
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Integer
name|fromPersistedFormat
init|=
name|codec
operator|.
name|fromPersistedFormat
argument_list|(
name|persistedFormat
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
argument_list|,
name|fromPersistedFormat
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

