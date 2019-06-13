begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ozone
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|powermock
operator|.
name|api
operator|.
name|mockito
operator|.
name|PowerMockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|powermock
operator|.
name|core
operator|.
name|classloader
operator|.
name|annotations
operator|.
name|PrepareForTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|powermock
operator|.
name|modules
operator|.
name|junit4
operator|.
name|PowerMockRunner
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * FilteredClassLoader test using mocks.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|PowerMockRunner
operator|.
name|class
argument_list|)
annotation|@
name|PrepareForTest
argument_list|(
block|{
name|FilteredClassLoader
operator|.
name|class
block|,
name|OzoneFSInputStream
operator|.
name|class
block|}
argument_list|)
DECL|class|TestFilteredClassLoader
specifier|public
class|class
name|TestFilteredClassLoader
block|{
annotation|@
name|Test
DECL|method|testFilteredClassLoader ()
specifier|public
name|void
name|testFilteredClassLoader
parameter_list|()
block|{
name|PowerMockito
operator|.
name|mockStatic
argument_list|(
name|System
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|System
operator|.
name|getenv
argument_list|(
literal|"HADOOP_OZONE_DELEGATED_CLASSES"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"org.apache.hadoop.fs.ozone.OzoneFSInputStream"
argument_list|)
expr_stmt|;
name|ClassLoader
name|currentClassLoader
init|=
name|TestFilteredClassLoader
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|URL
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ClassLoader
name|classLoader
init|=
operator|new
name|FilteredClassLoader
argument_list|(
name|urls
operator|.
name|toArray
argument_list|(
operator|new
name|URL
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|currentClassLoader
argument_list|)
decl_stmt|;
try|try
block|{
name|classLoader
operator|.
name|loadClass
argument_list|(
literal|"org.apache.hadoop.fs.ozone.OzoneFSInputStream"
argument_list|)
expr_stmt|;
name|ClassLoader
name|expectedClassLoader
init|=
name|OzoneFSInputStream
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedClassLoader
argument_list|,
name|currentClassLoader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

