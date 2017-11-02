begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.partition
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|partition
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
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configurable
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|BinaryComparable
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
name|IOUtils
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
name|NullWritable
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
name|SequenceFile
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
name|RawComparator
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
name|mapreduce
operator|.
name|Job
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
name|mapreduce
operator|.
name|Partitioner
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
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Partitioner effecting a total order by reading split points from  * an externally generated source.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|TotalOrderPartitioner
specifier|public
class|class
name|TotalOrderPartitioner
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Partitioner
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|partitions
specifier|private
name|Node
name|partitions
decl_stmt|;
DECL|field|DEFAULT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_PATH
init|=
literal|"_partition.lst"
decl_stmt|;
DECL|field|PARTITIONER_PATH
specifier|public
specifier|static
specifier|final
name|String
name|PARTITIONER_PATH
init|=
literal|"mapreduce.totalorderpartitioner.path"
decl_stmt|;
DECL|field|MAX_TRIE_DEPTH
specifier|public
specifier|static
specifier|final
name|String
name|MAX_TRIE_DEPTH
init|=
literal|"mapreduce.totalorderpartitioner.trie.maxdepth"
decl_stmt|;
DECL|field|NATURAL_ORDER
specifier|public
specifier|static
specifier|final
name|String
name|NATURAL_ORDER
init|=
literal|"mapreduce.totalorderpartitioner.naturalorder"
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TotalOrderPartitioner
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TotalOrderPartitioner ()
specifier|public
name|TotalOrderPartitioner
parameter_list|()
block|{ }
comment|/**    * Read in the partition file and build indexing data structures.    * If the keytype is {@link org.apache.hadoop.io.BinaryComparable} and    *<tt>total.order.partitioner.natural.order</tt> is not false, a trie    * of the first<tt>total.order.partitioner.max.trie.depth</tt>(2) + 1 bytes    * will be built. Otherwise, keys will be located using a binary search of    * the partition keyset using the {@link org.apache.hadoop.io.RawComparator}    * defined for this job. The input file must be sorted with the same    * comparator and contain {@link Job#getNumReduceTasks()} - 1 keys.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// keytype from conf not static
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|String
name|parts
init|=
name|getPartitionFile
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|partFile
init|=
operator|new
name|Path
argument_list|(
name|parts
argument_list|)
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
operator|(
name|DEFAULT_PATH
operator|.
name|equals
argument_list|(
name|parts
argument_list|)
operator|)
condition|?
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
comment|// assume in DistributedCache
else|:
name|partFile
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|K
argument_list|>
name|keyClass
init|=
operator|(
name|Class
argument_list|<
name|K
argument_list|>
operator|)
name|job
operator|.
name|getMapOutputKeyClass
argument_list|()
decl_stmt|;
name|K
index|[]
name|splitPoints
init|=
name|readPartitions
argument_list|(
name|fs
argument_list|,
name|partFile
argument_list|,
name|keyClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitPoints
operator|.
name|length
operator|!=
name|job
operator|.
name|getNumReduceTasks
argument_list|()
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Wrong number of partitions in keyset"
argument_list|)
throw|;
block|}
name|RawComparator
argument_list|<
name|K
argument_list|>
name|comparator
init|=
operator|(
name|RawComparator
argument_list|<
name|K
argument_list|>
operator|)
name|job
operator|.
name|getSortComparator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|splitPoints
operator|.
name|length
operator|-
literal|1
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|splitPoints
index|[
name|i
index|]
argument_list|,
name|splitPoints
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Split points are out of order"
argument_list|)
throw|;
block|}
block|}
name|boolean
name|natOrder
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|NATURAL_ORDER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|natOrder
operator|&&
name|BinaryComparable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|keyClass
argument_list|)
condition|)
block|{
name|partitions
operator|=
name|buildTrie
argument_list|(
operator|(
name|BinaryComparable
index|[]
operator|)
name|splitPoints
argument_list|,
literal|0
argument_list|,
name|splitPoints
operator|.
name|length
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
comment|// Now that blocks of identical splitless trie nodes are
comment|// represented reentrantly, and we develop a leaf for any trie
comment|// node with only one split point, the only reason for a depth
comment|// limit is to refute stack overflow or bloat in the pathological
comment|// case where the split points are long and mostly look like bytes
comment|// iii...iixii...iii   .  Therefore, we make the default depth
comment|// limit large but not huge.
name|conf
operator|.
name|getInt
argument_list|(
name|MAX_TRIE_DEPTH
argument_list|,
literal|200
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|partitions
operator|=
operator|new
name|BinarySearchNode
argument_list|(
name|splitPoints
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't read partitions file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|// by construction, we know if our keytype
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// is memcmp-able and uses the trie
DECL|method|getPartition (K key, V value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
return|return
name|partitions
operator|.
name|findPartition
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Set the path to the SequenceFile storing the sorted partition keyset.    * It must be the case that for<tt>R</tt> reduces, there are<tt>R-1</tt>    * keys in the SequenceFile.    */
DECL|method|setPartitionFile (Configuration conf, Path p)
specifier|public
specifier|static
name|void
name|setPartitionFile
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
name|p
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|PARTITIONER_PATH
argument_list|,
name|p
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the path to the SequenceFile storing the sorted partition keyset.    * @see #setPartitionFile(Configuration, Path)    */
DECL|method|getPartitionFile (Configuration conf)
specifier|public
specifier|static
name|String
name|getPartitionFile
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|PARTITIONER_PATH
argument_list|,
name|DEFAULT_PATH
argument_list|)
return|;
block|}
comment|/**    * Interface to the partitioner to locate a key in the partition keyset.    */
DECL|interface|Node
interface|interface
name|Node
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * Locate partition in keyset K, st [Ki..Ki+1) defines a partition,      * with implicit K0 = -inf, Kn = +inf, and |K| = #partitions - 1.      */
DECL|method|findPartition (T key)
name|int
name|findPartition
parameter_list|(
name|T
name|key
parameter_list|)
function_decl|;
block|}
comment|/**    * Base class for trie nodes. If the keytype is memcomp-able, this builds    * tries of the first<tt>total.order.partitioner.max.trie.depth</tt>    * bytes.    */
DECL|class|TrieNode
specifier|static
specifier|abstract
class|class
name|TrieNode
implements|implements
name|Node
argument_list|<
name|BinaryComparable
argument_list|>
block|{
DECL|field|level
specifier|private
specifier|final
name|int
name|level
decl_stmt|;
DECL|method|TrieNode (int level)
name|TrieNode
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
DECL|method|getLevel ()
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|level
return|;
block|}
block|}
comment|/**    * For types that are not {@link org.apache.hadoop.io.BinaryComparable} or    * where disabled by<tt>total.order.partitioner.natural.order</tt>,    * search the partition keyset with a binary search.    */
DECL|class|BinarySearchNode
class|class
name|BinarySearchNode
implements|implements
name|Node
argument_list|<
name|K
argument_list|>
block|{
DECL|field|splitPoints
specifier|private
specifier|final
name|K
index|[]
name|splitPoints
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|RawComparator
argument_list|<
name|K
argument_list|>
name|comparator
decl_stmt|;
DECL|method|BinarySearchNode (K[] splitPoints, RawComparator<K> comparator)
name|BinarySearchNode
parameter_list|(
name|K
index|[]
name|splitPoints
parameter_list|,
name|RawComparator
argument_list|<
name|K
argument_list|>
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|splitPoints
operator|=
name|splitPoints
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
DECL|method|findPartition (K key)
specifier|public
name|int
name|findPartition
parameter_list|(
name|K
name|key
parameter_list|)
block|{
specifier|final
name|int
name|pos
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|splitPoints
argument_list|,
name|key
argument_list|,
name|comparator
argument_list|)
operator|+
literal|1
decl_stmt|;
return|return
operator|(
name|pos
operator|<
literal|0
operator|)
condition|?
operator|-
name|pos
else|:
name|pos
return|;
block|}
block|}
comment|/**    * An inner trie node that contains 256 children based on the next    * character.    */
DECL|class|InnerTrieNode
class|class
name|InnerTrieNode
extends|extends
name|TrieNode
block|{
DECL|field|child
specifier|private
name|TrieNode
index|[]
name|child
init|=
operator|new
name|TrieNode
index|[
literal|256
index|]
decl_stmt|;
DECL|method|InnerTrieNode (int level)
name|InnerTrieNode
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|super
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
DECL|method|findPartition (BinaryComparable key)
specifier|public
name|int
name|findPartition
parameter_list|(
name|BinaryComparable
name|key
parameter_list|)
block|{
name|int
name|level
init|=
name|getLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|getLength
argument_list|()
operator|<=
name|level
condition|)
block|{
return|return
name|child
index|[
literal|0
index|]
operator|.
name|findPartition
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
name|child
index|[
literal|0xFF
operator|&
name|key
operator|.
name|getBytes
argument_list|()
index|[
name|level
index|]
index|]
operator|.
name|findPartition
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/**    * @param level        the tree depth at this node    * @param splitPoints  the full split point vector, which holds    *                     the split point or points this leaf node    *                     should contain    * @param lower        first INcluded element of splitPoints    * @param upper        first EXcluded element of splitPoints    * @return  a leaf node.  They come in three kinds: no split points     *          [and the findParttion returns a canned index], one split    *          point [and we compare with a single comparand], or more    *          than one [and we do a binary search].  The last case is    *          rare.    */
DECL|method|LeafTrieNodeFactory (int level, BinaryComparable[] splitPoints, int lower, int upper)
specifier|private
name|TrieNode
name|LeafTrieNodeFactory
parameter_list|(
name|int
name|level
parameter_list|,
name|BinaryComparable
index|[]
name|splitPoints
parameter_list|,
name|int
name|lower
parameter_list|,
name|int
name|upper
parameter_list|)
block|{
switch|switch
condition|(
name|upper
operator|-
name|lower
condition|)
block|{
case|case
literal|0
case|:
return|return
operator|new
name|UnsplitTrieNode
argument_list|(
name|level
argument_list|,
name|lower
argument_list|)
return|;
case|case
literal|1
case|:
return|return
operator|new
name|SinglySplitTrieNode
argument_list|(
name|level
argument_list|,
name|splitPoints
argument_list|,
name|lower
argument_list|)
return|;
default|default:
return|return
operator|new
name|LeafTrieNode
argument_list|(
name|level
argument_list|,
name|splitPoints
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|)
return|;
block|}
block|}
comment|/**    * A leaf trie node that scans for the key between lower..upper.    *     * We don't generate many of these now, since we usually continue trie-ing     * when more than one split point remains at this level. and we make different    * objects for nodes with 0 or 1 split point.    */
DECL|class|LeafTrieNode
specifier|private
class|class
name|LeafTrieNode
extends|extends
name|TrieNode
block|{
DECL|field|lower
specifier|final
name|int
name|lower
decl_stmt|;
DECL|field|upper
specifier|final
name|int
name|upper
decl_stmt|;
DECL|field|splitPoints
specifier|final
name|BinaryComparable
index|[]
name|splitPoints
decl_stmt|;
DECL|method|LeafTrieNode (int level, BinaryComparable[] splitPoints, int lower, int upper)
name|LeafTrieNode
parameter_list|(
name|int
name|level
parameter_list|,
name|BinaryComparable
index|[]
name|splitPoints
parameter_list|,
name|int
name|lower
parameter_list|,
name|int
name|upper
parameter_list|)
block|{
name|super
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|this
operator|.
name|lower
operator|=
name|lower
expr_stmt|;
name|this
operator|.
name|upper
operator|=
name|upper
expr_stmt|;
name|this
operator|.
name|splitPoints
operator|=
name|splitPoints
expr_stmt|;
block|}
DECL|method|findPartition (BinaryComparable key)
specifier|public
name|int
name|findPartition
parameter_list|(
name|BinaryComparable
name|key
parameter_list|)
block|{
specifier|final
name|int
name|pos
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|splitPoints
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|,
name|key
argument_list|)
operator|+
literal|1
decl_stmt|;
return|return
operator|(
name|pos
operator|<
literal|0
operator|)
condition|?
operator|-
name|pos
else|:
name|pos
return|;
block|}
block|}
DECL|class|UnsplitTrieNode
specifier|private
class|class
name|UnsplitTrieNode
extends|extends
name|TrieNode
block|{
DECL|field|result
specifier|final
name|int
name|result
decl_stmt|;
DECL|method|UnsplitTrieNode (int level, int value)
name|UnsplitTrieNode
parameter_list|(
name|int
name|level
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|value
expr_stmt|;
block|}
DECL|method|findPartition (BinaryComparable key)
specifier|public
name|int
name|findPartition
parameter_list|(
name|BinaryComparable
name|key
parameter_list|)
block|{
return|return
name|result
return|;
block|}
block|}
DECL|class|SinglySplitTrieNode
specifier|private
class|class
name|SinglySplitTrieNode
extends|extends
name|TrieNode
block|{
DECL|field|lower
specifier|final
name|int
name|lower
decl_stmt|;
DECL|field|mySplitPoint
specifier|final
name|BinaryComparable
name|mySplitPoint
decl_stmt|;
DECL|method|SinglySplitTrieNode (int level, BinaryComparable[] splitPoints, int lower)
name|SinglySplitTrieNode
parameter_list|(
name|int
name|level
parameter_list|,
name|BinaryComparable
index|[]
name|splitPoints
parameter_list|,
name|int
name|lower
parameter_list|)
block|{
name|super
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|this
operator|.
name|lower
operator|=
name|lower
expr_stmt|;
name|this
operator|.
name|mySplitPoint
operator|=
name|splitPoints
index|[
name|lower
index|]
expr_stmt|;
block|}
DECL|method|findPartition (BinaryComparable key)
specifier|public
name|int
name|findPartition
parameter_list|(
name|BinaryComparable
name|key
parameter_list|)
block|{
return|return
name|lower
operator|+
operator|(
name|key
operator|.
name|compareTo
argument_list|(
name|mySplitPoint
argument_list|)
operator|<
literal|0
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
block|}
comment|/**    * Read the cut points from the given IFile.    * @param fs The file system    * @param p The path to read    * @param keyClass The map output key class    * @param job The job config    * @throws IOException    */
comment|// matching key types enforced by passing in
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// map output key class
DECL|method|readPartitions (FileSystem fs, Path p, Class<K> keyClass, Configuration conf)
specifier|private
name|K
index|[]
name|readPartitions
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|,
name|Class
argument_list|<
name|K
argument_list|>
name|keyClass
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|SequenceFile
operator|.
name|Reader
name|reader
init|=
operator|new
name|SequenceFile
operator|.
name|Reader
argument_list|(
name|conf
argument_list|,
name|SequenceFile
operator|.
name|Reader
operator|.
name|file
argument_list|(
name|p
argument_list|)
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|K
argument_list|>
name|parts
init|=
operator|new
name|ArrayList
argument_list|<
name|K
argument_list|>
argument_list|()
decl_stmt|;
name|K
name|key
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|keyClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|key
operator|=
operator|(
name|K
operator|)
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|parts
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|key
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|keyClass
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|parts
operator|.
name|toArray
argument_list|(
operator|(
name|K
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|keyClass
argument_list|,
name|parts
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    *     * This object contains a TrieNodeRef if there is such a thing that    * can be repeated.  Two adjacent trie node slots that contain no     * split points can be filled with the same trie node, even if they    * are not on the same level.  See buildTreeRec, below.    *    */
DECL|class|CarriedTrieNodeRef
specifier|private
class|class
name|CarriedTrieNodeRef
block|{
DECL|field|content
name|TrieNode
name|content
decl_stmt|;
DECL|method|CarriedTrieNodeRef ()
name|CarriedTrieNodeRef
parameter_list|()
block|{
name|content
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Given a sorted set of cut points, build a trie that will find the correct    * partition quickly.    * @param splits the list of cut points    * @param lower the lower bound of partitions 0..numPartitions-1    * @param upper the upper bound of partitions 0..numPartitions-1    * @param prefix the prefix that we have already checked against    * @param maxDepth the maximum depth we will build a trie for    * @return the trie node that will divide the splits correctly    */
DECL|method|buildTrie (BinaryComparable[] splits, int lower, int upper, byte[] prefix, int maxDepth)
specifier|private
name|TrieNode
name|buildTrie
parameter_list|(
name|BinaryComparable
index|[]
name|splits
parameter_list|,
name|int
name|lower
parameter_list|,
name|int
name|upper
parameter_list|,
name|byte
index|[]
name|prefix
parameter_list|,
name|int
name|maxDepth
parameter_list|)
block|{
return|return
name|buildTrieRec
argument_list|(
name|splits
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|,
name|prefix
argument_list|,
name|maxDepth
argument_list|,
operator|new
name|CarriedTrieNodeRef
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * This is the core of buildTrie.  The interface, and stub, above, just adds    * an empty CarriedTrieNodeRef.      *     * We build trie nodes in depth first order, which is also in key space    * order.  Every leaf node is referenced as a slot in a parent internal    * node.  If two adjacent slots [in the DFO] hold leaf nodes that have    * no split point, then they are not separated by a split point either,     * because there's no place in key space for that split point to exist.    *     * When that happens, the leaf nodes would be semantically identical, and    * we reuse the object.  A single CarriedTrieNodeRef "ref" lives for the     * duration of the tree-walk.  ref carries a potentially reusable, unsplit    * leaf node for such reuse until a leaf node with a split arises, which     * breaks the chain until we need to make a new unsplit leaf node.    *     * Note that this use of CarriedTrieNodeRef means that for internal nodes,     * for internal nodes if this code is modified in any way we still need     * to make or fill in the subnodes in key space order.    */
DECL|method|buildTrieRec (BinaryComparable[] splits, int lower, int upper, byte[] prefix, int maxDepth, CarriedTrieNodeRef ref)
specifier|private
name|TrieNode
name|buildTrieRec
parameter_list|(
name|BinaryComparable
index|[]
name|splits
parameter_list|,
name|int
name|lower
parameter_list|,
name|int
name|upper
parameter_list|,
name|byte
index|[]
name|prefix
parameter_list|,
name|int
name|maxDepth
parameter_list|,
name|CarriedTrieNodeRef
name|ref
parameter_list|)
block|{
specifier|final
name|int
name|depth
init|=
name|prefix
operator|.
name|length
decl_stmt|;
comment|// We generate leaves for a single split point as well as for
comment|// no split points.
if|if
condition|(
name|depth
operator|>=
name|maxDepth
operator|||
name|lower
operator|>=
name|upper
operator|-
literal|1
condition|)
block|{
comment|// If we have two consecutive requests for an unsplit trie node, we
comment|// can deliver the same one the second time.
if|if
condition|(
name|lower
operator|==
name|upper
operator|&&
name|ref
operator|.
name|content
operator|!=
literal|null
condition|)
block|{
return|return
name|ref
operator|.
name|content
return|;
block|}
name|TrieNode
name|result
init|=
name|LeafTrieNodeFactory
argument_list|(
name|depth
argument_list|,
name|splits
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|)
decl_stmt|;
name|ref
operator|.
name|content
operator|=
name|lower
operator|==
name|upper
condition|?
name|result
else|:
literal|null
expr_stmt|;
return|return
name|result
return|;
block|}
name|InnerTrieNode
name|result
init|=
operator|new
name|InnerTrieNode
argument_list|(
name|depth
argument_list|)
decl_stmt|;
name|byte
index|[]
name|trial
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|prefix
argument_list|,
name|prefix
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// append an extra byte on to the prefix
name|int
name|currentBound
init|=
name|lower
decl_stmt|;
for|for
control|(
name|int
name|ch
init|=
literal|0
init|;
name|ch
operator|<
literal|0xFF
condition|;
operator|++
name|ch
control|)
block|{
name|trial
index|[
name|depth
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|ch
operator|+
literal|1
argument_list|)
expr_stmt|;
name|lower
operator|=
name|currentBound
expr_stmt|;
while|while
condition|(
name|currentBound
operator|<
name|upper
condition|)
block|{
if|if
condition|(
name|splits
index|[
name|currentBound
index|]
operator|.
name|compareTo
argument_list|(
name|trial
argument_list|,
literal|0
argument_list|,
name|trial
operator|.
name|length
argument_list|)
operator|>=
literal|0
condition|)
block|{
break|break;
block|}
name|currentBound
operator|+=
literal|1
expr_stmt|;
block|}
name|trial
index|[
name|depth
index|]
operator|=
operator|(
name|byte
operator|)
name|ch
expr_stmt|;
name|result
operator|.
name|child
index|[
literal|0xFF
operator|&
name|ch
index|]
operator|=
name|buildTrieRec
argument_list|(
name|splits
argument_list|,
name|lower
argument_list|,
name|currentBound
argument_list|,
name|trial
argument_list|,
name|maxDepth
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
comment|// pick up the rest
name|trial
index|[
name|depth
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xFF
expr_stmt|;
name|result
operator|.
name|child
index|[
literal|0xFF
index|]
operator|=
name|buildTrieRec
argument_list|(
name|splits
argument_list|,
name|lower
argument_list|,
name|currentBound
argument_list|,
name|trial
argument_list|,
name|maxDepth
argument_list|,
name|ref
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

