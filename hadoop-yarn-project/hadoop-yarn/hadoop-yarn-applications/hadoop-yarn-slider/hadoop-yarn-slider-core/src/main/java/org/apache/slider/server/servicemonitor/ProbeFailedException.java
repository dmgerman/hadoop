begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.servicemonitor
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|servicemonitor
package|;
end_package

begin_comment
comment|/**  * An exception to raise on a probe failure  */
end_comment

begin_class
DECL|class|ProbeFailedException
specifier|public
class|class
name|ProbeFailedException
extends|extends
name|Exception
block|{
DECL|field|status
specifier|public
specifier|final
name|ProbeStatus
name|status
decl_stmt|;
DECL|method|ProbeFailedException (String text, ProbeStatus status)
specifier|public
name|ProbeFailedException
parameter_list|(
name|String
name|text
parameter_list|,
name|ProbeStatus
name|status
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|text
operator|==
literal|null
condition|?
literal|"Probe Failed"
else|:
operator|(
name|text
operator|+
literal|": "
operator|)
operator|)
operator|+
name|status
argument_list|,
name|status
operator|.
name|getThrown
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
block|}
end_class

end_unit

