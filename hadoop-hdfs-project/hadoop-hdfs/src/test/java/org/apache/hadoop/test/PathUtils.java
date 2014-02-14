begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
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
name|Path
import|;
end_import

begin_class
DECL|class|PathUtils
specifier|public
class|class
name|PathUtils
block|{
DECL|method|getTestPath (Class<?> caller)
specifier|public
specifier|static
name|Path
name|getTestPath
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|caller
parameter_list|)
block|{
return|return
name|getTestPath
argument_list|(
name|caller
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getTestPath (Class<?> caller, boolean create)
specifier|public
specifier|static
name|Path
name|getTestPath
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|caller
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getTestDirName
argument_list|(
name|caller
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTestDir (Class<?> caller)
specifier|public
specifier|static
name|File
name|getTestDir
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|caller
parameter_list|)
block|{
return|return
name|getTestDir
argument_list|(
name|caller
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getTestDir (Class<?> caller, boolean create)
specifier|public
specifier|static
name|File
name|getTestDir
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|caller
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"target/test/data"
argument_list|)
operator|+
literal|"/"
operator|+
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|10
argument_list|)
argument_list|,
name|caller
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
DECL|method|getTestDirName (Class<?> caller)
specifier|public
specifier|static
name|String
name|getTestDirName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|caller
parameter_list|)
block|{
return|return
name|getTestDirName
argument_list|(
name|caller
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getTestDirName (Class<?> caller, boolean create)
specifier|public
specifier|static
name|String
name|getTestDirName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|caller
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
return|return
name|getTestDir
argument_list|(
name|caller
argument_list|,
name|create
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
block|}
end_class

end_unit

