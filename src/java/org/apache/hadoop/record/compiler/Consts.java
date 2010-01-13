begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record.compiler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
operator|.
name|compiler
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|record
operator|.
name|RecordInput
import|;
end_import

begin_comment
comment|/**  * const definitions for Record I/O compiler  *   * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Consts
specifier|public
class|class
name|Consts
block|{
comment|/** Cannot create a new instance */
DECL|method|Consts ()
specifier|private
name|Consts
parameter_list|()
block|{   }
comment|// prefix to use for variables in generated classes
DECL|field|RIO_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|RIO_PREFIX
init|=
literal|"_rio_"
decl_stmt|;
comment|// other vars used in generated classes
DECL|field|RTI_VAR
specifier|public
specifier|static
specifier|final
name|String
name|RTI_VAR
init|=
name|RIO_PREFIX
operator|+
literal|"recTypeInfo"
decl_stmt|;
DECL|field|RTI_FILTER
specifier|public
specifier|static
specifier|final
name|String
name|RTI_FILTER
init|=
name|RIO_PREFIX
operator|+
literal|"rtiFilter"
decl_stmt|;
DECL|field|RTI_FILTER_FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|RTI_FILTER_FIELDS
init|=
name|RIO_PREFIX
operator|+
literal|"rtiFilterFields"
decl_stmt|;
DECL|field|RECORD_OUTPUT
specifier|public
specifier|static
specifier|final
name|String
name|RECORD_OUTPUT
init|=
name|RIO_PREFIX
operator|+
literal|"a"
decl_stmt|;
DECL|field|RECORD_INPUT
specifier|public
specifier|static
specifier|final
name|String
name|RECORD_INPUT
init|=
name|RIO_PREFIX
operator|+
literal|"a"
decl_stmt|;
DECL|field|TAG
specifier|public
specifier|static
specifier|final
name|String
name|TAG
init|=
name|RIO_PREFIX
operator|+
literal|"tag"
decl_stmt|;
block|}
end_class

end_unit

