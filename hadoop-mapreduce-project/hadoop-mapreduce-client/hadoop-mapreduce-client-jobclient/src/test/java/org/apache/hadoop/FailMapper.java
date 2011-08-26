begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableComparable
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
name|mapred
operator|.
name|MapReduceBase
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
name|mapred
operator|.
name|Mapper
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
name|mapred
operator|.
name|OutputCollector
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
name|mapred
operator|.
name|Reporter
import|;
end_import

begin_comment
comment|// Mapper that fails
end_comment

begin_class
DECL|class|FailMapper
specifier|public
class|class
name|FailMapper
extends|extends
name|MapReduceBase
implements|implements
name|Mapper
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|,
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
block|{
DECL|method|map (WritableComparable key, Writable value, OutputCollector<WritableComparable, Writable> out, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Writable
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
name|out
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NOTE- the next line is required for the TestDebugScript test to succeed
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"failing map"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failing map"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

