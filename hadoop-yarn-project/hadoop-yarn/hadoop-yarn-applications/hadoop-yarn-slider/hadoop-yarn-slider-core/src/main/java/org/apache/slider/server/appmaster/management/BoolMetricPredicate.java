begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.management
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|management
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Gauge
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Metric
import|;
end_import

begin_comment
comment|/**  * A metric which takes a predicate and returns 1 if the predicate evaluates  * to true. The predicate is evaluated whenever the metric is read.  */
end_comment

begin_class
DECL|class|BoolMetricPredicate
specifier|public
class|class
name|BoolMetricPredicate
implements|implements
name|Metric
implements|,
name|Gauge
argument_list|<
name|Integer
argument_list|>
block|{
DECL|field|predicate
specifier|private
specifier|final
name|Eval
name|predicate
decl_stmt|;
DECL|method|BoolMetricPredicate (Eval predicate)
specifier|public
name|BoolMetricPredicate
parameter_list|(
name|Eval
name|predicate
parameter_list|)
block|{
name|this
operator|.
name|predicate
operator|=
name|predicate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue ()
specifier|public
name|Integer
name|getValue
parameter_list|()
block|{
return|return
name|predicate
operator|.
name|eval
argument_list|()
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|interface|Eval
specifier|public
interface|interface
name|Eval
block|{
DECL|method|eval ()
name|boolean
name|eval
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

