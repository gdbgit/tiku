<template>
  <el-container style="border: 1px solid #eee">
    <el-main>
      <el-row gutter="0">
        <el-col span="20">
          <el-button class="el-button" align="left" plain @click="jumpInput">录入题目</el-button>
          <!-- <el-button type="primary" plain>全选</el-button> -->
          <el-button type="success" plain @click="batchDelete(tableChecked)">批量删除</el-button>
          <el-button type="warning" plain @click="centerDialogVisible_batch = true">标签批量修改</el-button>
        </el-col>
        <el-col span="4">
          <el-input
            v-model="search"
            style="display: inline-block;width: 180px"
            placeholder="请输入搜索内容"
          ></el-input>
        </el-col>
      </el-row>
      <el-row>
        <el-col span="24">
          <div></div>
        </el-col>
      </el-row>
      <el-row>
        <el-table
          :data="tables"
          border
          stripe
          style="width: 100%"
          @selection-change="this.handleSelectionChange"
        >
          <el-table-column prop="id" type="selection" width="55"></el-table-column>
          <el-table-column fixed="left" prop="problem" label="问题" width="300"></el-table-column>
          <el-table-column prop="answer" label="答案" width="300"></el-table-column>
          <el-table-column prop="sound" label="语音" width="100"></el-table-column>
          <el-table-column prop="pictures" label="多图片" width="100"></el-table-column>
          <el-table-column label="选项">
            <el-table-column
              v-for="f in fieldInfo"
              v-bind:key="f"
              :prop="f.keyname"
              :label="f.title"
            ></el-table-column>
          </el-table-column>
          <el-table-column
            prop="tag"
            label="标签"
            width="220"
            :filter-method="filterTag"
            filter-placement="bottom-end"
          >
            <template slot-scope="scope">
              <el-tag
                v-for="(tagsrc,index) in scope.row.tag"
                v-bind:key="index"
                disable-transitions
              >{{tagsrc}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="250">
            <template slot-scope="scope">
              <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
              <el-button
                size="mini"
                @click="centerDialogVisible_single = true"
                v-on:click="showTags(scope.$index)"
              >修改标签</el-button>
              <el-button size="mini" type="danger" @click="handleDelete(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-row>
    </el-main>
    <el-footer align="center">
      <HistoryPagination
        :listSize="this.listSize"
        :handlechange="this.handlechange"
        :handleSizeChange="this.handleSizeChange"
        ref="pager"
      ></HistoryPagination>
    </el-footer>
    <el-dialog title="修改标签" :visible.sync="centerDialogVisible_single" width="30%" center>
      <div align="center">
        <el-select
          v-model="value"
          multiple
          filterable
          allow-create
          default-first-option
          size="medium"
          placeholder="请选择题目标签"
        >
          <!-- @change="modifyTag" -->
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </div>
      <el-row></el-row>
      <div align="center">
        <span slot="footer" class="dialog-footer">
          <el-button @click="centerDialogVisible_single = false">取 消</el-button>
          <el-button type="primary" @click="centerDialogVisible_single = false;modifyTag()">确 定</el-button>
        </span>
      </div>
    </el-dialog>
    <el-dialog title="批量修改标签" :visible.sync="centerDialogVisible_batch" width="30%" center>
      <div align="center">
        <el-select
          v-model="value_batch"
          multiple
          filterable
          allow-create
          default-first-option
          size="medium"
          placeholder="请选择题目标签"
        >
          <!-- @change="modifyTag" -->
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </div>
      <el-row></el-row>
      <div align="center">
        <span slot="footer" class="dialog-footer">
          <el-button @click="centerDialogVisible_batch = false">取 消</el-button>
          <el-button type="primary" @click="centerDialogVisible_batch = false;modifyBatchTags()">确 定</el-button>
        </span>
      </div>
    </el-dialog>
  </el-container>
</template>

<script>
import { getProblems, delProblem, changeProblem } from "../api/Problem";
import ProblemFullData from "../data/model/ProblemFullData";
import { getTagsList, addTags, delTag } from "../api/Tag";
import { AllFieldInfo } from "../data/mock/FiledInfoMock";
import { changePaper } from "../api/Paper";
import HistoryPagination from "./HistoryPagination";

//编辑操作
function handleEdit(index, row) {
  this.$store.commit("setLastPageNumber", this.currentPage);
  //转到ModifyProblem页面
  this.$router.push({
    path: "/ModifyProblem",
    //query对象获取问题和答案

    query: {
      modifyIndex: index
    }
  });
}
//删除单行问题
function handleDelete(index) {
  this.$confirm("确定删除该问题?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    let delId = [];
    delId.push(this.$store.state.allProblem[index].problem.id); //获取要删除的问题id
    delProblem(delId, b => {
      if (b.code === "ok") {
        this.getData(this.listPageNumber - this.del);
        this.$message({ type: "warning ", message: "删除成功!" });
      }
    });
  });
}
function handleSelectionChange(val) {
  this.tableChecked = val;
}
function batchDelete(rows) {
  //批量删除
  let delId = [];
  for (var i = 0; i < rows.length; i++) {
    delId.push(rows[i].id);
  }
  this.$confirm("确定批量删除问题?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  })
    .then(() => {
      delProblem(delId, b => {
        if (b.code === "ok") {
          this.del = delId.length === this.listLenght ? 1 : this.del;
          this.getData(this.listPageNumber - this.del);
          this.$message({ type: "success", message: "删除成功!" });
        }
      });
    })
    .catch(() => {
      this.$message({ type: "info", message: "已取消删除" });
    });
}
//批量修改标签
function modifyBatchTags() {
  let rows = this.tableChecked;
  let modifyProblems = [];
  for (var i = 0; i < this.tableData.length; i++) {
    //获取选中项id
    for (var j = 0; j < rows.length; j++) {
      if (this.tableData[i].problem === rows[j].problem) {
        //获取选中的问题id
        this.$store.state.allProblem[i].tags = [];
        this.value_batch.forEach(v => {
          this.$store.state.allProblem[i].tags.push({
            value: v
          });
        });
        modifyProblems.push(this.$store.state.allProblem[i]);
      }
    }
  }
  for (var k = 0; k < modifyProblems.length; k++) {
    changeProblem(modifyProblems[k], b => {
      if (b.code !== "ok") {
        this.$message({ type: "error", message: "修改失败! " + b.data });
      } else {
        this.$message({
          type: "success",
          message: "修改成功! 可去审核列表查看"
        });
      }
    });
  }
  this.del = modifyProblems.length === this.listLenght ? 1 : this.del;
  this.getData(this.listPageNumber - this.del);
}
function filterTag(value, row) {
  return row.tag === value;
}
function jumpInput() {

  //传递的参数用{{ $route.query.goodsId }}获取
  this.$store.commit("setLastPageNumber", this.currentPage);
  this.$router.push({ path: "/InputTiku" });
  //this.$router.go(-2)
  //后退两步
}
function handleClose(tag) {
  //标签上的叉
  this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
}
function handleInputConfirm() {
  //添加完标签之后的确定
  let inputValue = this.inputValue;
  if (inputValue) {
    this.dynamicTags.push(inputValue);
  }
  this.inputVisible = false;
  this.inputValue = "";
}

function handlechange(currentPage) {
  //获取题目
  this.currentPage = currentPage;
  this.getData(currentPage - 1);
}
function getData(currentPage) {
  var _this = this;
  let callback = (pd, size) => {
    this.listLenght = pd.length;
    this.del = pd.length === 1 ? 1 : 0;
    this.listPageNumber = currentPage;
    this.listSize = size * 10;
    var res = [];
    this.$store.commit("setNewProblems", pd);
    pd.filter(v => {
      let ts = [];
      if (v.tags !== null) {
        for (let i = 0; i < v.tags.length; i++) {
          ts.push(v.tags[i].value);
        }
      }
      if (v.answer === null) {
        v.answer = {
          answerText: ""
        };
      }
      let ress = {
        id: v.problem.id,
        problem: v.problem.problemText,
        answer: v.answer.answerText,
        pictures: "",
        sound: "",
        tag: ts
      };
      if (v.extData !== null && v.extData !== undefined) {
        Object.keys(v.extData).forEach(key => {
          ress[key] = v.extData[key];
        });
      }
      res.push(ress);
    });
    _this.tableData = res;
  };
  getProblems(currentPage, callback, 1);
}
//在修改标签窗口显示已有标签
function showTags(index) {
  let value_tmp = [];
  for (let i = 0; i < this.$store.state.allProblem[index].tags.length; i++) {
    value_tmp.push(this.$store.state.allProblem[index].tags[i].value); //获取store的标签
  }
  this.value = value_tmp;
  this.index_tmp = index;
}
//修改某一行问题的标签
function modifyTag() {
  let selectedProblem = this.$store.state.allProblem[this.index_tmp];
  selectedProblem.tags = [];
  this.value.forEach(v => {
    selectedProblem.tags.push({
      value: v
    });
  });
  changeProblem(selectedProblem, b => {
    if (b.code === "ok") {
      this.getData(this.listPageNumber - this.del);
      this.$message({
        type: "success",
        message: "修改成功! 可去审核列表查看"
      });
    } else {
      this.$message({ type: "error", message: "修改失败!" + b.data });
    }
  });
}
//获取全部标签（选择器下拉窗口里用）
function getTags() {
  var _this = this;
  let callback = (pd, size) => {
    this.$store.commit("setNewCommits", pd);
    pd.filter(v => {
      let ress = {
        value: v.value,
        label: v.value
      };
      this.options.push(ress);
    });
  };
  getTagsList(0, callback, 0);
}
function handleSizeChange(val) {}

export default {
  name: "TikuTable",
  components: { HistoryPagination },
  datas: [],
  methods: {
    handleEdit,
    handleSizeChange,
    handleDelete,
    handleSelectionChange,
    batchDelete,
    modifyBatchTags,
    filterTag,
    jumpInput,
    handleClose,
    handleInputConfirm,

    handlechange,
    getData,
    showTags,
    getTags,
    modifyTag
  },
  mounted: function() {
    this.getData(this.listPageNumber);
    this.getTags();

    let all = [];
    Object.keys(AllFieldInfo).forEach(key => {
      all.push({
        keyname: key,
        title: AllFieldInfo[key].title
      });
    });

    this.fieldInfo = all;
    if (this.$store.state.useLastPage) {
      this.$refs.pager.internalCurrentPage = this.$store.state.lastPageNumber;
      this.$store.commit("setUseLastPage", false); //使用保存的页数
    }
  },
  data() {
    return {
      /**
       * @listLenght 当前页面的 list 的的长度
       */
      listLenght: 0,
      /**
       * @del 用作是否为最后一页或者一页只有一个的标志位
       */
      del: 0,
      /**
       * @listPageNumber 当前页面是第几页
       */
      listPageNumber: 0,
      /**
       * @listSize  记录后天有多少页符合的数据
       */
      listSize: 0,
      tableChecked: [], //被选中的记录数据
      search: "",
      inputVisible: false,
      inputValue: "",
      centerDialogVisible_single: false,
      centerDialogVisible_batch: false,
      tableData: [],
      fieldInfo: [],
      options: [], //修改标签窗口选择器下拉的标签列表
      value: [], //修改标签窗口选择器里面的已选标签
      index_tmp: "", //选中修改标签的行index
      value_batch: [], //批量修改标签窗口选择器里面的已选标签
      currentPage: 0
    };
  },
  // 搜索操作
  computed: {
    tables() {
      const search = this.search;
      if (search) {
        // 检查指定数组中符合条件的所有元素。
        return this.tableData.filter(data => {
          return Object.keys(data).some(key => {
            // 没有找到返回-1；
            return (
              String(data[key])
                .toLowerCase()
                .indexOf(search.toLowerCase()) > -1
            );
          });
        });
      }
      return this.tableData;
    }
  }
};
</script>

<style scoped>
.el-row {
  margin-bottom: 10px;
  &:last-child {
    margin-bottom: 0;
  }
}
.el-col {
  border-radius: 4px;
}
</style>