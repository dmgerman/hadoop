begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * Constants for internal use in the org.apache.hadoop.fs.s3a module itself.  * Please don't refer to these outside of this module&amp; its tests.  * If you find you need to then either the code is doing something it  * should not, or these constants need to be uprated to being  * public and stable entries.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|InternalConstants
specifier|public
specifier|final
class|class
name|InternalConstants
block|{
DECL|method|InternalConstants ()
specifier|private
name|InternalConstants
parameter_list|()
block|{   }
comment|/**    * The known keys used in a standard openFile call.    * if there's a select marker in there then the keyset    * used becomes that of the select operation.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|STANDARD_OPENFILE_KEYS
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|STANDARD_OPENFILE_KEYS
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|Constants
operator|.
name|INPUT_FADVISE
argument_list|,
name|Constants
operator|.
name|READAHEAD_RANGE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

