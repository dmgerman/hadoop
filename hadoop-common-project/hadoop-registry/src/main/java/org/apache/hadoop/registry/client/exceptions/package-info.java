begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Registry Service Exceptions  *<p>  * These are the Registry-specific exceptions that may be raised during  * Registry operations.  *<p>  * Other exceptions may be raised, especially<code>IOExceptions</code>  * triggered by network problems, and<code>IllegalArgumentException</code>  * exceptions that may be raised if invalid (often null) arguments are passed  * to a method call.  *<p>  *   All exceptions in this package are derived from  *   {@link org.apache.hadoop.registry.client.exceptions.RegistryIOException}  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|exceptions
package|;
end_package

end_unit

