begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|FileSystem
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
name|azurebfs
operator|.
name|services
operator|.
name|AuthType
import|;
end_import

begin_comment
comment|/**  * Test finalize() method when "fs.abfs.impl.disable.cache" is enabled.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemFinalize
specifier|public
class|class
name|ITestAzureBlobFileSystemFinalize
extends|extends
name|AbstractAbfsScaleTest
block|{
DECL|field|DISABLE_ABFS_CACHE_KEY
specifier|static
specifier|final
name|String
name|DISABLE_ABFS_CACHE_KEY
init|=
literal|"fs.abfs.impl.disable.cache"
decl_stmt|;
DECL|field|DISABLE_ABFSSS_CACHE_KEY
specifier|static
specifier|final
name|String
name|DISABLE_ABFSSS_CACHE_KEY
init|=
literal|"fs.abfss.impl.disable.cache"
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemFinalize ()
specifier|public
name|ITestAzureBlobFileSystemFinalize
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFinalize ()
specifier|public
name|void
name|testFinalize
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Disable the cache for filesystem to make sure there is no reference.
name|Configuration
name|rawConfig
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|rawConfig
operator|.
name|setBoolean
argument_list|(
name|this
operator|.
name|getAuthType
argument_list|()
operator|==
name|AuthType
operator|.
name|SharedKey
condition|?
name|DISABLE_ABFS_CACHE_KEY
else|:
name|DISABLE_ABFSSS_CACHE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|AzureBlobFileSystem
name|fs
init|=
operator|(
name|AzureBlobFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|rawConfig
argument_list|)
decl_stmt|;
name|WeakReference
argument_list|<
name|Object
argument_list|>
name|ref
init|=
operator|new
name|WeakReference
argument_list|<
name|Object
argument_list|>
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|maxTries
init|=
literal|1000
decl_stmt|;
while|while
condition|(
name|ref
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|i
operator|<
name|maxTries
condition|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|runFinalization
argument_list|()
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"testFinalizer didn't get cleaned up within maxTries"
argument_list|,
name|ref
operator|.
name|get
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

