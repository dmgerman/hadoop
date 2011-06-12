begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_comment
comment|/**  * Simple class that holds the number formatters used in the slive application  */
end_comment

begin_class
DECL|class|Formatter
class|class
name|Formatter
block|{
DECL|field|NUMBER_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|NUMBER_FORMAT
init|=
literal|"###.###"
decl_stmt|;
DECL|field|decFormatter
specifier|private
specifier|static
name|NumberFormat
name|decFormatter
init|=
literal|null
decl_stmt|;
DECL|field|percFormatter
specifier|private
specifier|static
name|NumberFormat
name|percFormatter
init|=
literal|null
decl_stmt|;
comment|/**    * No construction allowed - only simple static accessor functions    */
DECL|method|Formatter ()
specifier|private
name|Formatter
parameter_list|()
block|{    }
comment|/**    * Gets a decimal formatter that has 3 decimal point precision    *     * @return NumberFormat formatter    */
DECL|method|getDecimalFormatter ()
specifier|static
specifier|synchronized
name|NumberFormat
name|getDecimalFormatter
parameter_list|()
block|{
if|if
condition|(
name|decFormatter
operator|==
literal|null
condition|)
block|{
name|decFormatter
operator|=
operator|new
name|DecimalFormat
argument_list|(
name|NUMBER_FORMAT
argument_list|)
expr_stmt|;
block|}
return|return
name|decFormatter
return|;
block|}
comment|/**    * Gets a percent formatter that has 3 decimal point precision    *     * @return NumberFormat formatter    */
DECL|method|getPercentFormatter ()
specifier|static
specifier|synchronized
name|NumberFormat
name|getPercentFormatter
parameter_list|()
block|{
if|if
condition|(
name|percFormatter
operator|==
literal|null
condition|)
block|{
name|percFormatter
operator|=
name|NumberFormat
operator|.
name|getPercentInstance
argument_list|()
expr_stmt|;
name|percFormatter
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
return|return
name|percFormatter
return|;
block|}
block|}
end_class

end_unit

