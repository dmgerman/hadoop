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
import|;
end_import

begin_comment
comment|/**  * A wrapper class to maven's ComparableVersion class, to comply  * with maven's version name string convention   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|VersionUtil
specifier|public
specifier|abstract
class|class
name|VersionUtil
block|{
comment|/**    * Compares two version name strings using maven's ComparableVersion class.    *    * @param version1    *          the first version to compare    * @param version2    *          the second version to compare    * @return a negative integer if version1 precedes version2, a positive    *         integer if version2 precedes version1, and 0 if and only if the two    *         versions are equal.    */
DECL|method|compareVersions (String version1, String version2)
specifier|public
specifier|static
name|int
name|compareVersions
parameter_list|(
name|String
name|version1
parameter_list|,
name|String
name|version2
parameter_list|)
block|{
name|ComparableVersion
name|v1
init|=
operator|new
name|ComparableVersion
argument_list|(
name|version1
argument_list|)
decl_stmt|;
name|ComparableVersion
name|v2
init|=
operator|new
name|ComparableVersion
argument_list|(
name|version2
argument_list|)
decl_stmt|;
return|return
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

