begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * MetricValue.java  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.spi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
operator|.
name|spi
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
comment|/**  * A Number that is either an absolute or an incremental amount.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MetricValue
specifier|public
class|class
name|MetricValue
block|{
DECL|field|ABSOLUTE
specifier|public
specifier|static
specifier|final
name|boolean
name|ABSOLUTE
init|=
literal|false
decl_stmt|;
DECL|field|INCREMENT
specifier|public
specifier|static
specifier|final
name|boolean
name|INCREMENT
init|=
literal|true
decl_stmt|;
DECL|field|isIncrement
specifier|private
name|boolean
name|isIncrement
decl_stmt|;
DECL|field|number
specifier|private
name|Number
name|number
decl_stmt|;
comment|/** Creates a new instance of MetricValue */
DECL|method|MetricValue (Number number, boolean isIncrement)
specifier|public
name|MetricValue
parameter_list|(
name|Number
name|number
parameter_list|,
name|boolean
name|isIncrement
parameter_list|)
block|{
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
name|this
operator|.
name|isIncrement
operator|=
name|isIncrement
expr_stmt|;
block|}
DECL|method|isIncrement ()
specifier|public
name|boolean
name|isIncrement
parameter_list|()
block|{
return|return
name|isIncrement
return|;
block|}
DECL|method|isAbsolute ()
specifier|public
name|boolean
name|isAbsolute
parameter_list|()
block|{
return|return
operator|!
name|isIncrement
return|;
block|}
DECL|method|getNumber ()
specifier|public
name|Number
name|getNumber
parameter_list|()
block|{
return|return
name|number
return|;
block|}
block|}
end_class

end_unit

