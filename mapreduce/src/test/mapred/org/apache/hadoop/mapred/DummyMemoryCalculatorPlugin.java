begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|util
operator|.
name|MemoryCalculatorPlugin
import|;
end_import

begin_comment
comment|/**  * Plugin class to test virtual and physical memories reported by TT. Use  * configuration items {@link #MAXVMEM_TESTING_PROPERTY} and  * {@link #MAXPMEM_TESTING_PROPERTY} to tell TT the total vmem and the total  * pmem.  */
end_comment

begin_class
DECL|class|DummyMemoryCalculatorPlugin
specifier|public
class|class
name|DummyMemoryCalculatorPlugin
extends|extends
name|MemoryCalculatorPlugin
block|{
comment|/** max vmem on the TT */
DECL|field|MAXVMEM_TESTING_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|MAXVMEM_TESTING_PROPERTY
init|=
literal|"mapred.tasktracker.maxvmem.testing"
decl_stmt|;
comment|/** max pmem on the TT */
DECL|field|MAXPMEM_TESTING_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|MAXPMEM_TESTING_PROPERTY
init|=
literal|"mapred.tasktracker.maxpmem.testing"
decl_stmt|;
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getVirtualMemorySize ()
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|MAXVMEM_TESTING_PROPERTY
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getPhysicalMemorySize ()
specifier|public
name|long
name|getPhysicalMemorySize
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|MAXPMEM_TESTING_PROPERTY
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

