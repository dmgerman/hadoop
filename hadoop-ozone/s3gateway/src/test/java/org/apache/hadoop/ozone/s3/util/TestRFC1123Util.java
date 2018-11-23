begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|temporal
operator|.
name|TemporalAccessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test for RFC1123 util.  */
end_comment

begin_class
DECL|class|TestRFC1123Util
specifier|public
class|class
name|TestRFC1123Util
block|{
annotation|@
name|Test
DECL|method|parse ()
specifier|public
name|void
name|parse
parameter_list|()
block|{
comment|//one digit day
name|String
name|dateStr
init|=
literal|"Mon, 5 Nov 2018 15:04:05 GMT"
decl_stmt|;
name|TemporalAccessor
name|date
init|=
name|RFC1123Util
operator|.
name|FORMAT
operator|.
name|parse
argument_list|(
name|dateStr
argument_list|)
decl_stmt|;
name|String
name|formatted
init|=
name|RFC1123Util
operator|.
name|FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
decl_stmt|;
comment|//two digits day
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Mon, 05 Nov 2018 15:04:05 GMT"
argument_list|,
name|formatted
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

