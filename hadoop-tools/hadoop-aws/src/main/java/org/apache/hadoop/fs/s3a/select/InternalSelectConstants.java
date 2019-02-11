begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.select
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
operator|.
name|select
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
name|fs
operator|.
name|s3a
operator|.
name|InternalConstants
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|select
operator|.
name|SelectConstants
operator|.
name|*
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
DECL|class|InternalSelectConstants
specifier|public
specifier|final
class|class
name|InternalSelectConstants
block|{
DECL|method|InternalSelectConstants ()
specifier|private
name|InternalSelectConstants
parameter_list|()
block|{   }
comment|/**    * An unmodifiable set listing the options    * supported in {@code openFile()}.    */
DECL|field|SELECT_OPTIONS
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|SELECT_OPTIONS
decl_stmt|;
comment|/*    * Build up the options, pulling in the standard set too.    */
static|static
block|{
comment|// when adding to this, please keep in alphabetical order after the
comment|// common options and the SQL.
name|HashSet
argument_list|<
name|String
argument_list|>
name|options
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|SELECT_SQL
argument_list|,
name|SELECT_ERRORS_INCLUDE_SQL
argument_list|,
name|SELECT_INPUT_COMPRESSION
argument_list|,
name|SELECT_INPUT_FORMAT
argument_list|,
name|SELECT_OUTPUT_FORMAT
argument_list|,
name|CSV_INPUT_COMMENT_MARKER
argument_list|,
name|CSV_INPUT_HEADER
argument_list|,
name|CSV_INPUT_INPUT_FIELD_DELIMITER
argument_list|,
name|CSV_INPUT_QUOTE_CHARACTER
argument_list|,
name|CSV_INPUT_QUOTE_ESCAPE_CHARACTER
argument_list|,
name|CSV_INPUT_RECORD_DELIMITER
argument_list|,
name|CSV_OUTPUT_FIELD_DELIMITER
argument_list|,
name|CSV_OUTPUT_QUOTE_CHARACTER
argument_list|,
name|CSV_OUTPUT_QUOTE_ESCAPE_CHARACTER
argument_list|,
name|CSV_OUTPUT_QUOTE_FIELDS
argument_list|,
name|CSV_OUTPUT_RECORD_DELIMITER
argument_list|)
argument_list|)
decl_stmt|;
name|options
operator|.
name|addAll
argument_list|(
name|InternalConstants
operator|.
name|STANDARD_OPENFILE_KEYS
argument_list|)
expr_stmt|;
name|SELECT_OPTIONS
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

