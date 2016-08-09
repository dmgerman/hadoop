begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|mapreduce
operator|.
name|MRJobConfig
import|;
end_import

begin_comment
comment|/**  * A class that contains utility methods for MR Job configuration.  */
end_comment

begin_class
DECL|class|MRJobConfUtil
specifier|public
specifier|final
class|class
name|MRJobConfUtil
block|{
DECL|field|REDACTION_REPLACEMENT_VAL
specifier|public
specifier|static
specifier|final
name|String
name|REDACTION_REPLACEMENT_VAL
init|=
literal|"*********(redacted)"
decl_stmt|;
comment|/**    * Redact job configuration properties.    * @param conf the job configuration to redact    */
DECL|method|redact (final Configuration conf)
specifier|public
specifier|static
name|void
name|redact
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
for|for
control|(
name|String
name|prop
range|:
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_REDACTED_PROPERTIES
argument_list|)
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|prop
argument_list|,
name|REDACTION_REPLACEMENT_VAL
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * There is no reason to instantiate this utility class.    */
DECL|method|MRJobConfUtil ()
specifier|private
name|MRJobConfUtil
parameter_list|()
block|{   }
block|}
end_class

end_unit

