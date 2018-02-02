begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
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
name|Path
import|;
end_import

begin_comment
comment|/**  * Interface for excluding files from DistCp.  *  */
end_comment

begin_class
DECL|class|CopyFilter
specifier|public
specifier|abstract
class|class
name|CopyFilter
block|{
comment|/**    * Default initialize method does nothing.    */
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
block|{}
comment|/**    * Predicate to determine if a file can be excluded from copy.    *    * @param path a Path to be considered for copying    * @return boolean, true to copy, false to exclude    */
DECL|method|shouldCopy (Path path)
specifier|public
specifier|abstract
name|boolean
name|shouldCopy
parameter_list|(
name|Path
name|path
parameter_list|)
function_decl|;
comment|/**    * Public factory method which returns the appropriate implementation of    * CopyFilter.    *    * @param conf DistCp configuration    * @return An instance of the appropriate CopyFilter    */
DECL|method|getCopyFilter (Configuration conf)
specifier|public
specifier|static
name|CopyFilter
name|getCopyFilter
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|filtersFilename
init|=
name|conf
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_FILTERS_FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|filtersFilename
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|TrueCopyFilter
argument_list|()
return|;
block|}
else|else
block|{
name|String
name|filterFilename
init|=
name|conf
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_FILTERS_FILE
argument_list|)
decl_stmt|;
return|return
operator|new
name|RegexCopyFilter
argument_list|(
name|filterFilename
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

