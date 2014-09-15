begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Any key type that is comparable at native side must implement this interface.  *  * A native comparator function should have the ComparatorPtr type:  *<code>  *   typedef int (*ComparatorPtr)(const char * src, uint32_t srcLength,  *   const char * dest,  uint32_t destLength);  *</code>  * Keys are in serialized format at native side. The function has passed in  * the keys' locations and lengths such that we can compare them in the same  * logic as their Java comparator.  *  * For example, a HiveKey serialized as an int field (containing the length of  * raw bytes) + raw bytes.  * When comparing two HiveKeys, we first read the length field and then  * compare the raw bytes by invoking the BytesComparator provided by our library.  * We pass the location and length of raw bytes into BytesComparator.  *  *<code>  *   int HivePlatform::HiveKeyComparator(const char * src, uint32_t srcLength,  *   const char * dest, uint32_t destLength) {  *     uint32_t sl = bswap(*(uint32_t*)src);  *     uint32_t dl = bswap(*(uint32_t*)dest);  *     return NativeObjectFactory::BytesComparator(src + 4, sl, dest + 4, dl);  *   }  *</code>  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|INativeComparable
specifier|public
interface|interface
name|INativeComparable
block|{ }
end_interface

end_unit

