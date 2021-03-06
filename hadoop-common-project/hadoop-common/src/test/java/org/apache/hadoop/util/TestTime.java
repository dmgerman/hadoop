begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
comment|/**  * A JUnit test to test {@link Time}.  */
end_comment

begin_class
DECL|class|TestTime
specifier|public
class|class
name|TestTime
block|{
DECL|field|DATE_FORMAT
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|SimpleDateFormat
argument_list|>
name|DATE_FORMAT
init|=
operator|new
name|ThreadLocal
argument_list|<
name|SimpleDateFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|SimpleDateFormat
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss,SSSZ"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Test formatTime.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testFormatTime ()
specifier|public
name|void
name|testFormatTime
parameter_list|()
block|{
name|long
name|time
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Time
operator|.
name|formatTime
argument_list|(
name|time
argument_list|)
argument_list|,
name|DATE_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

