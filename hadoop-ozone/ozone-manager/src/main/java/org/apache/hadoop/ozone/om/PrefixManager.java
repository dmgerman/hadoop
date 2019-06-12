begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmPrefixInfo
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

begin_comment
comment|/**  * Handles prefix commands.  * //TODO: support OzoneManagerFS for ozfs optimization using prefix tree.  */
end_comment

begin_interface
DECL|interface|PrefixManager
specifier|public
interface|interface
name|PrefixManager
extends|extends
name|IOzoneAcl
block|{
comment|/**    * Returns the metadataManager.    * @return OMMetadataManager.    */
DECL|method|getMetadataManager ()
name|OMMetadataManager
name|getMetadataManager
parameter_list|()
function_decl|;
comment|/**    * Get the list of path components that match with obj's path.    * longest prefix.    * Note: the number of the entries include a root "/"    * so if you have a longtest prefix path /a/b/c/    * the returned list will be ["/", "a", "b", "c"]    * @param path ozone object path    * @return list of longest path components that matches obj's path.    */
DECL|method|getLongestPrefixPath (String path)
name|List
argument_list|<
name|OmPrefixInfo
argument_list|>
name|getLongestPrefixPath
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

