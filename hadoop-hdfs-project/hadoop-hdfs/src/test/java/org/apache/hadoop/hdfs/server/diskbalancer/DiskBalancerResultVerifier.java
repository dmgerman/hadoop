begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|TypeSafeMatcher
import|;
end_import

begin_comment
comment|/**  * Helps in verifying test results.  */
end_comment

begin_class
DECL|class|DiskBalancerResultVerifier
specifier|public
class|class
name|DiskBalancerResultVerifier
extends|extends
name|TypeSafeMatcher
argument_list|<
name|DiskBalancerException
argument_list|>
block|{
DECL|field|expectedResult
specifier|private
specifier|final
name|DiskBalancerException
operator|.
name|Result
name|expectedResult
decl_stmt|;
DECL|method|DiskBalancerResultVerifier (DiskBalancerException.Result expectedResult)
name|DiskBalancerResultVerifier
parameter_list|(
name|DiskBalancerException
operator|.
name|Result
name|expectedResult
parameter_list|)
block|{
name|this
operator|.
name|expectedResult
operator|=
name|expectedResult
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matchesSafely (DiskBalancerException exception)
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|DiskBalancerException
name|exception
parameter_list|)
block|{
return|return
operator|(
name|this
operator|.
name|expectedResult
operator|==
name|exception
operator|.
name|getResult
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|describeTo (Description description)
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
literal|"expects Result: "
argument_list|)
operator|.
name|appendValue
argument_list|(
name|this
operator|.
name|expectedResult
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

