begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|FileSystemCounter
specifier|public
enum|enum
name|FileSystemCounter
block|{
DECL|enumConstant|BYTES_READ
name|BYTES_READ
block|,
DECL|enumConstant|BYTES_WRITTEN
name|BYTES_WRITTEN
block|,
DECL|enumConstant|READ_OPS
name|READ_OPS
block|,
DECL|enumConstant|LARGE_READ_OPS
name|LARGE_READ_OPS
block|,
DECL|enumConstant|WRITE_OPS
name|WRITE_OPS
block|,
DECL|enumConstant|BYTES_READ_EC
name|BYTES_READ_EC
block|, }
end_enum

end_unit

