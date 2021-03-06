begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

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
name|List
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_comment
comment|/**  * This type has been deprecated in favor of  * {@link org.apache.hadoop.util.ApplicationClassLoader}. All new uses of  * ApplicationClassLoader should use that type instead.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
annotation|@
name|Deprecated
DECL|class|ApplicationClassLoader
specifier|public
class|class
name|ApplicationClassLoader
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ApplicationClassLoader
block|{
DECL|method|ApplicationClassLoader (URL[] urls, ClassLoader parent, List<String> systemClasses)
specifier|public
name|ApplicationClassLoader
parameter_list|(
name|URL
index|[]
name|urls
parameter_list|,
name|ClassLoader
name|parent
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|systemClasses
parameter_list|)
block|{
name|super
argument_list|(
name|urls
argument_list|,
name|parent
argument_list|,
name|systemClasses
argument_list|)
expr_stmt|;
block|}
DECL|method|ApplicationClassLoader (String classpath, ClassLoader parent, List<String> systemClasses)
specifier|public
name|ApplicationClassLoader
parameter_list|(
name|String
name|classpath
parameter_list|,
name|ClassLoader
name|parent
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|systemClasses
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|super
argument_list|(
name|classpath
argument_list|,
name|parent
argument_list|,
name|systemClasses
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

