begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|PathCapabilities
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|PathCapabilitiesSupport
specifier|public
class|class
name|PathCapabilitiesSupport
block|{
comment|/**    * Validate the arguments to    * {@link PathCapabilities#hasPathCapability(Path, String)}.    * @param path path to query the capability of.    * @param capability non-null, non-empty string to query the path for support.    * @return the string to use in a switch statement.    * @throws IllegalArgumentException if a an argument is invalid.    */
DECL|method|validatePathCapabilityArgs ( final Path path, final String capability)
specifier|public
specifier|static
name|String
name|validatePathCapabilityArgs
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|String
name|capability
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|path
operator|!=
literal|null
argument_list|,
literal|"null path"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|capability
operator|!=
literal|null
argument_list|,
literal|"capability parameter is null"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|capability
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"capability parameter is empty string"
argument_list|)
expr_stmt|;
return|return
name|capability
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
return|;
block|}
block|}
end_class

end_unit

