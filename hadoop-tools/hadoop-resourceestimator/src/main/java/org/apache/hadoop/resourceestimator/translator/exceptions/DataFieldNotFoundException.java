begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.translator.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|translator
operator|.
name|exceptions
package|;
end_package

begin_comment
comment|/**  * Exception thrown when job attributes are not found.  */
end_comment

begin_class
DECL|class|DataFieldNotFoundException
specifier|public
class|class
name|DataFieldNotFoundException
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|684069387367879218L
decl_stmt|;
DECL|method|DataFieldNotFoundException (final String log)
specifier|public
name|DataFieldNotFoundException
parameter_list|(
specifier|final
name|String
name|log
parameter_list|)
block|{
name|super
argument_list|(
literal|"Fail to extract data fields properly from "
operator|+
name|log
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

