begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.rpc
package|package
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
name|rpc
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|OzoneConfigKeys
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
name|*
import|;
end_import

begin_comment
comment|/**  * Test class for {@link OzoneKMSUtil}.  * */
end_comment

begin_class
DECL|class|TestOzoneKMSUtil
specifier|public
class|class
name|TestOzoneKMSUtil
block|{
DECL|field|config
specifier|private
name|OzoneConfiguration
name|config
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|config
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_SECURITY_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getKeyProvider ()
specifier|public
name|void
name|getKeyProvider
parameter_list|()
block|{
try|try
block|{
name|OzoneKMSUtil
operator|.
name|getKeyProvider
argument_list|(
name|config
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"KMS serverProviderUri is "
operator|+
literal|"not configured."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

