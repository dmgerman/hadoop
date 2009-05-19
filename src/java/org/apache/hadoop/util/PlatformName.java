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

begin_comment
comment|/**  * A helper class for getting build-info of the java-vm.   *   */
end_comment

begin_class
DECL|class|PlatformName
specifier|public
class|class
name|PlatformName
block|{
comment|/**    * The complete platform 'name' to identify the platform as     * per the java-vm.    */
DECL|field|platformName
specifier|private
specifier|static
specifier|final
name|String
name|platformName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|+
literal|"-"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.arch"
argument_list|)
operator|+
literal|"-"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|)
decl_stmt|;
comment|/**    * Get the complete platform as per the java-vm.    * @return returns the complete platform as per the java-vm.    */
DECL|method|getPlatformName ()
specifier|public
specifier|static
name|String
name|getPlatformName
parameter_list|()
block|{
return|return
name|platformName
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|platformName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

