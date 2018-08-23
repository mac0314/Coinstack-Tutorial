<template>
  <div>
    <center>
      <input style="font-size:50px;" v-model="inputText" placeholder="여기를 수정해보세요" autofocus>
      <br>
      <br>
      <br>
      <Button v-bind:buttonId='2' v-bind:value='"전송"' class="button-yes" v-on:click_2='saveData(inputText)'></Button>
      <br>
      <br>
      <span style="font-size:50px; font-color:white;">{{inputText}}</span>
      <br>
      <br>
      <br>
      <span style="font-size:50px; font-color:white;">{{outputText}}</span>
      <br>
      <br>
      <br>
    </center>
  </div>
</template>

<script>
/* eslint-disable */
import Button from '@/components/interfaces/Button'
import ToggleSwitch from '@/components/interfaces/ToggleSwitch'
export default {
  name: 'Index',
  components: {
    Button,
    ToggleSwitch
  },
  beforeMount: function () {
    
  },
  mounted : function () {
    this.loadData()
  },
  data () {
    return {
      options: { },
      inputText: "",
      outputText: ""
    }
  },
  methods: {
    init: function () {
      this.options = { }

      this.options.apikey = this.apiKey
      this.options.keytype = this.keyType


    },
    saveData: function (textData) {
      const baseURI = this.baseURI
      const accessToken = localStorage.getItem('access_token')

      let data = {}

      data.message = this.inputText

      console.log(data)

      this.$http.post(
        `${baseURI}/api/message`,
        data,
        {
          headers: {
            //access_token: accessToken
          }
        }
      )
      .then((response) => {
        console.log(response)

        this.outputText = response.data.msg
      })
      .catch((error) => {
        console.log(error)
      })
    },
    loadData: function () {
      const baseURI = this.baseURI
      //const accessToken = localStorage.getItem('access_token')
      
      this.$http.get(
        `${baseURI}/api/message`,
        {
          headers: {
            //access_token: accessToken
          }
        }
      )
      .then((response) => {
        console.log(response)
        console.log(response.data.result_data)
        // const data = response.result_data
        this.outputText = response.data.result_data
      })
      .catch((error) => {
        console.log(error)
      })
    }
  },
  computed: {
    baseURI: function () {
      return this.$store.getters.baseURI
    }
  }
}
</script>

<!-- Add 'scoped' attribute to limit CSS to this component only -->
<style scoped>

.button-yes {
  background-color: #4CAF50; /* Green */
}

.button-no {
  background-color: #f44336; /* Red */
}

.button-on {
  background-color: #e7e7e7; color: black; /* Gray */
}

.button-off {
  background-color: #555555; /* Gray */
}

/* Hoverable Buttons */
.button {
    -webkit-transition-duration: 0.4s; /* Safari */
    transition-duration: 0.4s;
}
.button:hover {
    background-color: #e7e7e7; /* Green */
    color: white;
}
</style>
