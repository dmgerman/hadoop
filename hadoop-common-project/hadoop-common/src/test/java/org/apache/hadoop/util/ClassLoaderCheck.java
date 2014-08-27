begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_class
DECL|class|ClassLoaderCheck
specifier|public
class|class
name|ClassLoaderCheck
block|{
comment|/**    * Verifies the class is loaded by the right classloader.    */
DECL|method|checkClassLoader (Class cls, boolean shouldBeLoadedByAppClassLoader)
specifier|public
specifier|static
name|void
name|checkClassLoader
parameter_list|(
name|Class
name|cls
parameter_list|,
name|boolean
name|shouldBeLoadedByAppClassLoader
parameter_list|)
block|{
name|boolean
name|loadedByAppClassLoader
init|=
name|cls
operator|.
name|getClassLoader
argument_list|()
operator|instanceof
name|ApplicationClassLoader
decl_stmt|;
if|if
condition|(
operator|(
name|shouldBeLoadedByAppClassLoader
operator|&&
operator|!
name|loadedByAppClassLoader
operator|)
operator|||
operator|(
operator|!
name|shouldBeLoadedByAppClassLoader
operator|&&
name|loadedByAppClassLoader
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"incorrect classloader used"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

