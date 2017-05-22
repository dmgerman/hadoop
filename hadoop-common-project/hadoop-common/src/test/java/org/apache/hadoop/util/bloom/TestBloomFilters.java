begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util.bloom
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|bloom
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
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractCollection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|util
operator|.
name|bloom
operator|.
name|BloomFilterCommonTester
operator|.
name|BloomFilterTestStrategy
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
name|util
operator|.
name|hash
operator|.
name|Hash
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_class
DECL|class|TestBloomFilters
specifier|public
class|class
name|TestBloomFilters
block|{
DECL|field|numInsertions
name|int
name|numInsertions
init|=
literal|1000
decl_stmt|;
DECL|field|bitSize
name|int
name|bitSize
init|=
name|BloomFilterCommonTester
operator|.
name|optimalNumOfBits
argument_list|(
name|numInsertions
argument_list|,
literal|0.03
argument_list|)
decl_stmt|;
DECL|field|hashFunctionNumber
name|int
name|hashFunctionNumber
init|=
literal|5
decl_stmt|;
DECL|field|FALSE_POSITIVE_UNDER_1000
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|?
extends|extends
name|AbstractCollection
argument_list|<
name|Key
argument_list|>
argument_list|>
name|FALSE_POSITIVE_UNDER_1000
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|Hash
operator|.
name|JENKINS_HASH
argument_list|,
operator|new
name|AbstractCollection
argument_list|<
name|Key
argument_list|>
argument_list|()
block|{
specifier|final
name|ImmutableList
argument_list|<
name|Key
argument_list|>
name|falsePositive
init|=
name|ImmutableList
operator|.
expr|<
name|Key
operator|>
name|of
argument_list|(
operator|new
name|Key
argument_list|(
literal|"99"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Key
argument_list|(
literal|"963"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Key
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|falsePositive
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|falsePositive
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|,
name|Hash
operator|.
name|MURMUR_HASH
argument_list|,
operator|new
name|AbstractCollection
argument_list|<
name|Key
argument_list|>
argument_list|()
block|{
specifier|final
name|ImmutableList
argument_list|<
name|Key
argument_list|>
name|falsePositive
init|=
name|ImmutableList
operator|.
expr|<
name|Key
operator|>
name|of
argument_list|(
operator|new
name|Key
argument_list|(
literal|"769"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Key
argument_list|(
literal|"772"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Key
argument_list|(
literal|"810"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Key
argument_list|(
literal|"874"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Key
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|falsePositive
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|falsePositive
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
DECL|enum|Digits
specifier|private
enum|enum
name|Digits
block|{
DECL|enumConstant|ODD
DECL|enumConstant|EVEN
name|ODD
argument_list|(
literal|1
argument_list|)
block|,
name|EVEN
argument_list|(
literal|0
argument_list|)
block|;
DECL|field|start
name|int
name|start
decl_stmt|;
DECL|method|Digits (int start)
name|Digits
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
DECL|method|getStart ()
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDynamicBloomFilter ()
specifier|public
name|void
name|testDynamicBloomFilter
parameter_list|()
block|{
name|int
name|hashId
init|=
name|Hash
operator|.
name|JENKINS_HASH
decl_stmt|;
name|Filter
name|filter
init|=
operator|new
name|DynamicBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|Hash
operator|.
name|JENKINS_HASH
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|BloomFilterCommonTester
operator|.
name|of
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|)
operator|.
name|withFilterInstance
argument_list|(
name|filter
argument_list|)
operator|.
name|withTestCases
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|BloomFilterTestStrategy
operator|.
name|KEY_TEST_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|ADD_KEYS_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|EXCEPTIONS_CHECK_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|WRITE_READ_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|ODD_EVEN_ABSENT_STRATEGY
argument_list|)
argument_list|)
operator|.
name|test
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"testDynamicBloomFilter error "
argument_list|,
name|filter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCountingBloomFilter ()
specifier|public
name|void
name|testCountingBloomFilter
parameter_list|()
block|{
name|int
name|hashId
init|=
name|Hash
operator|.
name|JENKINS_HASH
decl_stmt|;
name|CountingBloomFilter
name|filter
init|=
operator|new
name|CountingBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
decl_stmt|;
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|48
block|,
literal|48
block|}
argument_list|)
decl_stmt|;
name|filter
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CountingBloomFilter.membership error "
argument_list|,
name|filter
operator|.
name|membershipTest
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CountingBloomFilter.approximateCount error"
argument_list|,
name|filter
operator|.
name|approximateCount
argument_list|(
name|key
argument_list|)
operator|==
literal|1
argument_list|)
expr_stmt|;
name|filter
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CountingBloomFilter.approximateCount error"
argument_list|,
name|filter
operator|.
name|approximateCount
argument_list|(
name|key
argument_list|)
operator|==
literal|2
argument_list|)
expr_stmt|;
name|filter
operator|.
name|delete
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CountingBloomFilter.membership error "
argument_list|,
name|filter
operator|.
name|membershipTest
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|delete
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"CountingBloomFilter.membership error "
argument_list|,
name|filter
operator|.
name|membershipTest
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CountingBloomFilter.approximateCount error"
argument_list|,
name|filter
operator|.
name|approximateCount
argument_list|(
name|key
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|BloomFilterCommonTester
operator|.
name|of
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|)
operator|.
name|withFilterInstance
argument_list|(
name|filter
argument_list|)
operator|.
name|withTestCases
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|BloomFilterTestStrategy
operator|.
name|KEY_TEST_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|ADD_KEYS_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|EXCEPTIONS_CHECK_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|ODD_EVEN_ABSENT_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|WRITE_READ_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|FILTER_OR_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|FILTER_XOR_STRATEGY
argument_list|)
argument_list|)
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetouchedBloomFilterSpecific ()
specifier|public
name|void
name|testRetouchedBloomFilterSpecific
parameter_list|()
block|{
name|int
name|numInsertions
init|=
literal|1000
decl_stmt|;
name|int
name|hashFunctionNumber
init|=
literal|5
decl_stmt|;
name|ImmutableSet
argument_list|<
name|Integer
argument_list|>
name|hashes
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|Hash
operator|.
name|MURMUR_HASH
argument_list|,
name|Hash
operator|.
name|JENKINS_HASH
argument_list|)
decl_stmt|;
for|for
control|(
name|Integer
name|hashId
range|:
name|hashes
control|)
block|{
name|RetouchedBloomFilter
name|filter
init|=
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
decl_stmt|;
name|checkOnAbsentFalsePositive
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|,
name|filter
argument_list|,
name|Digits
operator|.
name|ODD
argument_list|,
name|RemoveScheme
operator|.
name|MAXIMUM_FP
argument_list|)
expr_stmt|;
name|filter
operator|.
name|and
argument_list|(
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
expr_stmt|;
name|checkOnAbsentFalsePositive
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|,
name|filter
argument_list|,
name|Digits
operator|.
name|EVEN
argument_list|,
name|RemoveScheme
operator|.
name|MAXIMUM_FP
argument_list|)
expr_stmt|;
name|filter
operator|.
name|and
argument_list|(
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
expr_stmt|;
name|checkOnAbsentFalsePositive
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|,
name|filter
argument_list|,
name|Digits
operator|.
name|ODD
argument_list|,
name|RemoveScheme
operator|.
name|MINIMUM_FN
argument_list|)
expr_stmt|;
name|filter
operator|.
name|and
argument_list|(
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
expr_stmt|;
name|checkOnAbsentFalsePositive
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|,
name|filter
argument_list|,
name|Digits
operator|.
name|EVEN
argument_list|,
name|RemoveScheme
operator|.
name|MINIMUM_FN
argument_list|)
expr_stmt|;
name|filter
operator|.
name|and
argument_list|(
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
expr_stmt|;
name|checkOnAbsentFalsePositive
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|,
name|filter
argument_list|,
name|Digits
operator|.
name|ODD
argument_list|,
name|RemoveScheme
operator|.
name|RATIO
argument_list|)
expr_stmt|;
name|filter
operator|.
name|and
argument_list|(
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
expr_stmt|;
name|checkOnAbsentFalsePositive
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|,
name|filter
argument_list|,
name|Digits
operator|.
name|EVEN
argument_list|,
name|RemoveScheme
operator|.
name|RATIO
argument_list|)
expr_stmt|;
name|filter
operator|.
name|and
argument_list|(
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkOnAbsentFalsePositive (int hashId, int numInsertions, final RetouchedBloomFilter filter, Digits digits, short removeSchema)
specifier|private
name|void
name|checkOnAbsentFalsePositive
parameter_list|(
name|int
name|hashId
parameter_list|,
name|int
name|numInsertions
parameter_list|,
specifier|final
name|RetouchedBloomFilter
name|filter
parameter_list|,
name|Digits
name|digits
parameter_list|,
name|short
name|removeSchema
parameter_list|)
block|{
name|AbstractCollection
argument_list|<
name|Key
argument_list|>
name|falsePositives
init|=
name|FALSE_POSITIVE_UNDER_1000
operator|.
name|get
argument_list|(
name|hashId
argument_list|)
decl_stmt|;
if|if
condition|(
name|falsePositives
operator|==
literal|null
condition|)
name|Assert
operator|.
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"false positives for hash %d not founded"
argument_list|,
name|hashId
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|addFalsePositive
argument_list|(
name|falsePositives
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|digits
operator|.
name|getStart
argument_list|()
init|;
name|i
operator|<
name|numInsertions
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|filter
operator|.
name|add
argument_list|(
operator|new
name|Key
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Key
name|key
range|:
name|falsePositives
control|)
block|{
name|filter
operator|.
name|selectiveClearing
argument_list|(
name|key
argument_list|,
name|removeSchema
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
operator|-
name|digits
operator|.
name|getStart
argument_list|()
init|;
name|i
operator|<
name|numInsertions
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|assertFalse
argument_list|(
literal|" testRetouchedBloomFilterAddFalsePositive error "
operator|+
name|i
argument_list|,
name|filter
operator|.
name|membershipTest
argument_list|(
operator|new
name|Key
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFiltersWithJenkinsHash ()
specifier|public
name|void
name|testFiltersWithJenkinsHash
parameter_list|()
block|{
name|int
name|hashId
init|=
name|Hash
operator|.
name|JENKINS_HASH
decl_stmt|;
name|BloomFilterCommonTester
operator|.
name|of
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|)
operator|.
name|withFilterInstance
argument_list|(
operator|new
name|BloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
operator|.
name|withFilterInstance
argument_list|(
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
operator|.
name|withTestCases
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|BloomFilterTestStrategy
operator|.
name|KEY_TEST_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|ADD_KEYS_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|EXCEPTIONS_CHECK_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|ODD_EVEN_ABSENT_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|WRITE_READ_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|FILTER_OR_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|FILTER_AND_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|FILTER_XOR_STRATEGY
argument_list|)
argument_list|)
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFiltersWithMurmurHash ()
specifier|public
name|void
name|testFiltersWithMurmurHash
parameter_list|()
block|{
name|int
name|hashId
init|=
name|Hash
operator|.
name|MURMUR_HASH
decl_stmt|;
name|BloomFilterCommonTester
operator|.
name|of
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|)
operator|.
name|withFilterInstance
argument_list|(
operator|new
name|BloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
operator|.
name|withFilterInstance
argument_list|(
operator|new
name|RetouchedBloomFilter
argument_list|(
name|bitSize
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
argument_list|)
operator|.
name|withTestCases
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|BloomFilterTestStrategy
operator|.
name|KEY_TEST_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|ADD_KEYS_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|EXCEPTIONS_CHECK_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|ODD_EVEN_ABSENT_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|WRITE_READ_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|FILTER_OR_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|FILTER_AND_STRATEGY
argument_list|,
name|BloomFilterTestStrategy
operator|.
name|FILTER_XOR_STRATEGY
argument_list|)
argument_list|)
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFiltersWithLargeVectorSize ()
specifier|public
name|void
name|testFiltersWithLargeVectorSize
parameter_list|()
block|{
name|int
name|hashId
init|=
name|Hash
operator|.
name|MURMUR_HASH
decl_stmt|;
name|Filter
name|filter
init|=
operator|new
name|BloomFilter
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|hashFunctionNumber
argument_list|,
name|hashId
argument_list|)
decl_stmt|;
name|BloomFilterCommonTester
operator|.
name|of
argument_list|(
name|hashId
argument_list|,
name|numInsertions
argument_list|)
operator|.
name|withFilterInstance
argument_list|(
name|filter
argument_list|)
operator|.
name|withTestCases
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|BloomFilterTestStrategy
operator|.
name|WRITE_READ_STRATEGY
argument_list|)
argument_list|)
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNot ()
specifier|public
name|void
name|testNot
parameter_list|()
block|{
name|BloomFilter
name|bf
init|=
operator|new
name|BloomFilter
argument_list|(
literal|8
argument_list|,
literal|1
argument_list|,
name|Hash
operator|.
name|JENKINS_HASH
argument_list|)
decl_stmt|;
name|bf
operator|.
name|bits
operator|=
name|BitSet
operator|.
name|valueOf
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x95
block|}
argument_list|)
expr_stmt|;
name|BitSet
name|origBitSet
init|=
operator|(
name|BitSet
operator|)
name|bf
operator|.
name|bits
operator|.
name|clone
argument_list|()
decl_stmt|;
name|bf
operator|.
name|not
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"BloomFilter#not should have inverted all bits"
argument_list|,
name|bf
operator|.
name|bits
operator|.
name|intersects
argument_list|(
name|origBitSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

